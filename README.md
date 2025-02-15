# Overview

This project will help me broaden my knowledge of other IDEs, languages, and data communication types.

This is a Church of Jesus-Christ of Latter-day Saints primary class helper application. It will track students prayers and articles of faith learning/completion. It will also be able to be data-synced with other android devices using NFC taps.

Be able to transfer data to/from team teacher associated with our primary students

[Iteration 3 Demo Video](https://youtu.be/Evhk4hj7JPk)

# Network Communication

This application network uses Peer to Peer communication. It's purpose is to Sync student data between team teachers. With the tap of the phones, data can be transferred from one phone to another and vice versa.

Even though this subject said to use TCP or UDP, I didn't. Instead I picked something I was much more interested in and that is NFC (Near Field Communication). NFC communication uses NFC Data Exchange Format (NDEF) messages to exchange data. NDEF is a binary format that is commonly used in NFC devices (like smartphones) and NFC tags. The main data container defined by NDEF is called an NDEF message. NDEF messages consist of one or more NDEF records of different types.

The messages being sent and received between the two devices is plain text in a JSON format. The "tag" message contains a language code, and for that i'm using en-US (or English in the United States)

# Development Environment

* IDE
  * Android Studio 
  * Gradle
* Mobile App
  * Android Framework
  * Room DB Framework
  * Kotlin
* NFC
  * Android Nfc Adapter
  * TECH and TAG detection
  * Send and Receive functionality

# Useful Websites

* [Android NFC Basics](https://developer.android.com/develop/connectivity/nfc/nfc)
* [Android NFC Adapter Guide](https://developer.android.com/reference/android/nfc/NfcAdapter)

# Future Work

* There are several obsolete methods. I need to determine what replaces them
* Android resolution kicked my butt this iteration. I need to learn how to adjust so that the contents will fit the screen. Watch the demo and you'll see what I mean
* Because of time, I didn't not incorporate "newest wins". Instead when the app receives the data, it removes existing data and writes the data from the NFC content
* Android theme is still kicking me. Dark vs Light. It seems as though android is easy, but all the details are very time consuming for the noob.