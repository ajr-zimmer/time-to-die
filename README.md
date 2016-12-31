# Time to Die
## Motivation
To create a motivational app that embraces the concept of *memento mori*.
My goal is to remind the user of their eventual expiration date. My hope is that they will realize that they have one awesome life to live, so they had better get around to living it!

## Setup
The repo is bundled as a standard Android Studio Project. Clone the repo, import the project into Android Studio, and get tinkering!

## What Is *Time to Die*?
*Time to Die* grabs relevant user information in order to calculate how much time they have left to live. The app aims to provide a positive outlook on life, and is meant to encourage the user to not waste their time. This could be a powerful tool for encouraging the user to be more [intrinsically motivated](https://en.wikipedia.org/wiki/Motivation#Incentive_theories:_intrinsic_and_extrinsic_motivation).

## How Does It Work?
*Time to Die* will collect the user's current country of residence, their date of birth, and their sex. This information is used in a REST call made to an API that returns the user's remaining life in years. The remaining life is a rough estimate, and is based off of life expectancy using the demographic factors provided. The years left are converted into more precise measures of time (i.e. days, hours, minutes, seconds). A countdown screen will be displayed upon completion of the
previous calculations.

## Acknowledgements
- [World Population API](http://api.population.io/) for the country list and life expectancy information
- [Icons8](https://icons8.com) for the skull vector used as the logo for *Time to Die*
- Suleiman's [blog post](http://blog.grafixartist.com/onboarding-android-viewpager-google-way/) on a tabbed onboarding activity inspired the activity for capturing user information. His GitHub can be found [here](https://github.com/Suleiman19)

## Author
[Andrew Zimmer](https://github.com/ajr-zimmer)
