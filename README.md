![](https://github.com/covid19risk/covidwatch-android/workflows/Develop%20Branch%20CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=covid19risk_covidwatch-android-en&metric=alert_status)](https://sonarcloud.io/dashboard?id=covid19risk_covidwatch-android-en)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# CovidWatch Android POC
Android POC for www.covid-watch.org

## Who are we? What is this app?

This repository is focused on the implementation of the Android version of the Covid Watch app. Our goals are to:
- Allow users to anonymously record interactions with others using the Google/Apple exposures notification api for Android
    (https://github.com/google/exposure-notifications-android)
- Notify users if someone they've interacted with in the past 2 weeks indicates they've tested positive for COVID-19 (again, anonymously!)
- Offer a seamless UX to complete all of the above!

The current version of the Figma we're working towards: (https://www.figma.com/file/I4OOjT4VhsSberDIAZTkcH/Covid-Watch---App-v2-(dev)?node-id=0%3A1


## Setup

Clone this repo from the `develop` branch:

git clone git@github.com:covid19risk/covidwatch-android.git

Open the project in Android Studio. Install onto a phone of yours with the `app` configuration, and you're free to explore the app! Its optimal to install on 2 phones as much of the behavior of the app depends on 2 phones interacting.

**Note:** You cannot run this app on an emulator! We are dependent on Bluetooth being on and active, and most standard Android emulators do not have Bluetooth drivers.

## Looking to contribute?

- Run on your own device to explore the UX. Look at the [Figma](https://www.figma.com/file/I4OOjT4VhsSberDIAZTkcH/Covid-Watch---App-v2-(dev)?node-id=0%3A1) for what the UX should look like. If you have any feedback/find any problems, create an issue!
- Look at https://blog.google/documents/68/Android_Exposure_Notification_API_documentation_v1.2.pdf for existing issues. If you see something you want to work on, assign yourself to it, set it to in progress, and make a PR to the `develop` branch.

## FAQ

What is the anonymous protocol for communication between phones? How does it work and who designed it?

Covid Watch uses Google/Android Exposure Notification, a decentralized, privacy-first contact tracing API implemented in Google and Apple phone operation systems. You can read more about it in the Google design document at https://blog.google/documents/68/Android_Exposure_Notification_API_documentation_v1.3.1.pdf

What's this repository vs the other repositories in the covid19risk Organization?

This is the repository for development of the front-facing Android mobile app for Covid Watch, including the UX, and backend services. 

## Contributors

- Madi Myrzabek (@madim)
- Milen Marinov (@BurningAXE)
- James Taylor (@jamesjmtaylor)
- Pavlo (@Apisov)
- Madhava (@madhavajay)
- Nitin Kumar (@nkumarcc, nkumarcc@gmail.com)
- Hayden Raddiford (@haydenridd)
- Enrico Grillo (@redbasset)
- Susan Crayne (@crayne)

## Join the cause!

Interested in volunteering with Covid Watch? Check out our [get involved page](https://covid-watch.org/collaborate) and send us an email at contact@covid-watch.org!

