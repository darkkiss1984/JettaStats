# JettaStats

This is a customized app designed for EU VW Jetta, based on AAStats ( https://github.com/martoreto/aastats )

Using :

https://github.com/martoreto/aauto-sdk (The piece of code which made OEM apps for AndroidAuto possible in the first place - Good Job!)

https://github.com/martoreto/aauto-vex-vag ( Used for reading telemetry data from the car, using the Vendor Extensions channel. )




* display gauges for rpm / speed , oil temp., outside temp. , current gear.

* log all the available telemetry to storage in JSON format,

* notify when oil reaches operating temperature,

* beep when maneuvering and the steering wheel crosses zero angle.

* notify when car recommends shifting gear ( up or down ) - in progress

## Requirements ( From @martoreto AAStats)

1. You need to build it yourself. ;)

   * You also need to rename the application package name (in ``app/build.gradle``) and [obtain ``google-services.json``](https://developers.google.com/mobile/add), and put it in ``app/``.

1. Install the aa-vex-vag.apk ( https://github.com/martoreto/aauto-vex-vag )
