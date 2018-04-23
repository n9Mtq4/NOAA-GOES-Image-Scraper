# NOAA Goes 16 Image Scraper

## About
This is a tiny program that scrapes and downloads images from 
NOAA's GOES east satellite. As of December 18, 2017, GOES-13 has
been retired from the GOES east position and been replaced with
GOES-16. This program is an updated version of [GOES-13](https://github.com/n9Mtq4/NOAA-Goes-13-image-scraper)
that works with GOES-16.
The point of this project is to be able to keep a locally
stored database of these images, so you can look back further than
the ~44 image limit of the online directory list.

NOAA's site with the images can be found [here](https://www.star.nesdis.noaa.gov/GOES/index.php).

## Running
1. Obtain a compiled jar file: See the "Building from source" instructions or head over the [releases page](https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper/releases).
2. cd to the directory containing the jar file. The images will also be downloaded in this directory.
3. run the JarFileName.jar with "java -jar JarFileMa,e.jar".
4. The images will appear in a newly created "./img/" directory.

## License
This program is copyrighted to Will Bresnahan or n9Mtq4 under the MIT License. More info in the [LICENSE File](https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper/blob/master/LICENSE).

## Building from source
1. Clone or download the source code
2. Extract the code if you downloaded the zip file
3. cd to the directory with the code
4. Either run "./gradlew build" on unix systems or "gradlew.bat build" on windows
5. Your shiny new jar will be located in "build/libs/"


## Recommended ffmpeg options for timelapse
`ffmpeg -framerate 30 -pattern_type glob -i '*.jpg' -c:v libx264 -b:v 20MB -maxrate 20MB -bufsize 100MB video.mp4`
