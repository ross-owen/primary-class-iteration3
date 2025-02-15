package com.myowencode.primaryclass.fragments.list

import android.media.RingtoneManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myowencode.primaryclass.R
import com.myowencode.primaryclass.db.Student
import com.myowencode.primaryclass.db.StudentViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentListFragment : Fragment(), NfcAdapter.ReaderCallback {

    private val languageCode: String = "en-US"
    private val sampleJson: String = "[{\"firstName\":\"Katie\",\"lastName\":\"Smith\",\"gender\":\"female\",\"birthDay\":\"Nov 10\"},{\"firstName\":\"Lisa\",\"lastName\":\"Masters\",\"gender\":\"female\",\"birthDay\":\"Oct 03\"},{\"firstName\":\"Maquel\",\"lastName\":\"Corbett\",\"gender\":\"female\",\"birthDay\":\"Oct 20\"},{\"firstName\":\"Ross\",\"lastName\":\"Owen\",\"gender\":\"male\",\"birthDay\":\"Jan 19\"},{\"firstName\":\"Tyrell\",\"lastName\":\"Owen\",\"gender\":\"male\",\"birthDay\":\"Aug 05\"}]"

    private lateinit var viewModel: StudentViewModel

    private var nfcAdapter: NfcAdapter? = null

    private var nfcDialog: AlertDialog? = null
    private var receivingNfcEnabled: Boolean = false
    private var sendingNfcEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_student_list, container, false)
        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.create_student_button)
            .setOnClickListener {
                findNavController().navigate(R.id.action_studentListFragment_to_createStudentFragment)
            }

        val adapter = StudentListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.student_recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
        viewModel.students.observe(viewLifecycleOwner, Observer { students ->
            adapter.setStudentList(students)
        })

        nfcAdapter = NfcAdapter.getDefaultAdapter(context)

        // only show the nfc menu item if nfc is enabled
        if (nfcAdapter?.isEnabled == true) {
            setHasOptionsMenu(true)
            createNfcDialog()
        }

        return view
    }

    /**
     * This method gets triggered when an NFC comes into range
     * Depending on the mode we're in (send or receive), we will
     * either transfer day to the NFC signal or receive from it
     */
    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag);

        try {
            // need to connect to the nfc definition of the tag whether we send or receive
            ndef.connect()
            if (sendingNfcEnabled) {
                sendDataThroughNfc(ndef)
            } else if (receivingNfcEnabled) {
                receiveDataFromNfc(ndef)
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, "NFC connection is not set", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("ROSS", "A pretty bad exception happened: ${e.message}", e)
            // do not die
        } finally {
            // close the nfc definition connection regardless of failure or not
            try {
                ndef.close();
            } catch (e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error closing the NFC connection", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun receiveDataFromNfc(ndef: Ndef) {
        // lock this so as not to allow multiple send/receives happening at the same time
        // the sendingNfcEnabled boolean will be flipped to false and result in the subsequent
        // threads to simply return
        synchronized(this) {
            if (!receivingNfcEnabled) {
                return
            }
            // to read the nfc content, create a string builder.
            // even though there's likely only one nfc definition message record
            // we'll loop through them anyway
            val content = StringBuilder()
            // if the records exist, loop through them and build the string content
            // this will be json if it's our stream
            ndef.ndefMessage?.records?.forEach { record ->
                val line = String(record.payload.clone())
                try {
                    // when the data was created, we used en-US (see `languageCode` above
                    // so we need to split on it and only take the right most part of the data string
                    val data = line.split(languageCode)[1]
                    content.append(data)
                } catch (e: IndexOutOfBoundsException) {
                    Log.d("ROSS", "I'm guessing this wasn't my tag: ${line}")
                }
            }
            // if we were able to get content from the nfc record(s) the string builder will
            // contain that data then convert this data and store
            if (content.isNotEmpty()) {
                Log.d("ROSS", content.toString())
                storeStudents(sampleJson)
//                storeStudents(content.toString())
            }

            receivingNfcEnabled = false;
            nfcDialog?.dismiss()
        }
    }

    private fun sendDataThroughNfc(ndef: Ndef) {
        // lock this so as not to allow multiple send/receives happening at the same time
        // the sendingNfcEnabled boolean will be flipped to false and result in the subsequent
        // threads to simply return
        synchronized(this) {
            if (!sendingNfcEnabled) {
                return
            }

            val record = NdefRecord.createTextRecord(languageCode, getStudentsJson());
            val message = NdefMessage(record)
            try {
                ndef.writeNdefMessage(message);

                activity?.runOnUiThread {
                    Toast.makeText(context, "Data transferred successfully", Toast.LENGTH_LONG).show()
                }

                try {
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    RingtoneManager.getRingtone(context, notification).play();
                } catch (e: Exception) {
                    Toast.makeText(context, "Sound manager broken", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("ROSS", "Something blew up: ${e.message}", e)
            }

            sendingNfcEnabled = false
            nfcDialog?.dismiss()
        }
    }

    // this method will get triggered if the application was idle, then became active
    // it will cause the activity to refresh
    override fun onResume() {
        super.onResume()
        if (nfcAdapter?.isEnabled == true) {
            val options = Bundle()
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            // we don't need all of these types. TODO: decide which type suits our needs
            // currently we are using NFC_B for NFC tag testing
            nfcAdapter?.enableReaderMode(
                activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V,
                options
            )
        }
    }

    // this method gets called when the app goes into idle mode (is no longer the active app on the device)
    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity);
    }

    // TODO: learn what replaces this and fix it
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nfc_menu, menu)
    }

    // TODO: learn what replaces this and fix it
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_nfc) {
            nfcDialog?.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNfcDialog() {
        nfcDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("NFC Transfer")
            .setMessage("Do you want to send or receive data via NFC transfer?")
            .setCancelable(true)
            .setNegativeButton(R.string.send, null)
            .setPositiveButton(R.string.receive, null)
            .setNeutralButton(getString(R.string.nevermind)) { _, _ ->
                sendingNfcEnabled = false
                receivingNfcEnabled = false
            }
            .create()


        nfcDialog?.setOnShowListener {

            nfcDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                sendingNfcEnabled = false
                receivingNfcEnabled = true
                Toast.makeText(requireContext(), "Tap devices to begin receiving", Toast.LENGTH_SHORT).show()
            }

            nfcDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setOnClickListener {
                sendingNfcEnabled = true
                receivingNfcEnabled = false
                Toast.makeText(requireContext(), "Tap devices to begin sending", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getStudentsJson() : String {
        val students: MutableList<StudentDto> = mutableListOf()
        viewModel.students.value?.forEach { student ->
            val dto = StudentDto(student.firstName, student.lastName, student.gender, student.getBirthday())
            students.add(dto)
        }
        return Json.encodeToString(students)
    }

    private fun storeStudents(json : String) {
        try {
            val results = Json.decodeFromString<List<StudentDto>>(json)
            if (results.isNotEmpty()) {
                viewModel.students.value?.forEach { student ->
                    Log.d("ROSS", "Removing student: ${student.firstName}")
                    viewModel.delete(student)
                }

                results.forEach { student ->
                    viewModel.upsert(Student(0, student.firstName, student.lastName, student.gender, getBirthDate(student.birthDay)))
                    Log.d("ROSS", "created ${student.firstName}")
                }
            }
        } catch (e: Exception) {
            Log.e("ROSS", e.message, e)
        }
    }

    private fun getBirthDate(birthDay : String) : Date? {
        if (birthDay.isEmpty()) {
            return null
        }
        val formatter: DateFormat = SimpleDateFormat("MMM dd yyyy", Locale.US)
        try {
            return formatter.parse(birthDay + " 2000")
        } catch (e: Exception) {
            Log.e("ROSS", "unable to parse birthday: $birthDay 2000")
            return null
        }
    }
}