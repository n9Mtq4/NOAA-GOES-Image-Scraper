# NOAA GOES-East (GOES-16) Image Scraper

## About
This is a tiny program that scrapes and downloads images from 
NOAA's GOES East satellite. As of December 18, 2017, GOES-13 has
been retired from the GOES east position and been replaced with
GOES-16. This program is an updated version of [GOES-13](https://github.com/n9Mtq4/NOAA-Goes-13-image-scraper)
that works with GOES-16.
The point of this project is to be able to keep a locally
stored database of these images, so you can look back further than
the image limit of the online directory list.

NOAA's site with the images can be found [here](https://www.star.nesdis.noaa.gov/GOES/index.php).

## Running
1. Obtain a compiled jar file: See the "Building from source" instructions or head over the [releases page](https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper/releases).
2. cd to the directory containing the jar file. The images will also be downloaded in this directory.
3. run the JarFileName.jar with `java -jar JarFileName.jar \[OPTIONS\]`.
4. The images will appear in a newly created "./imgs/" directory.

## Options
```
 -b,--band <arg>             selects the color/band  (run --bands for list
                             of types)
    --bands                  prints a list of bands
    --checksleeptime <arg>   the time between checking if sleep time has
                             passed
    --help                   prints this help message
 -o,--output <arg>           selects the output directory for the images
 -r,--resolution <arg>       selects the image resolution to download (run
                             --resolutions for list of resolutions)
    --resolutions            prints a list of resolutions
    --sleeptime <arg>        the time between downloading images
 -t,--type <arg>             the type of image (run --types for list of
                             types)
    --types                  prints a list of types

```

#### Bands
```
Band        Description
GeoColor    True Color daytime, multispectral IR at night
1           0.47 µm, Blue - Visible
2           0.64 µm, Red - Visible
3           0.86 µm, Veggie - Near IR
4           1.37 µm, Cirrus - Near IR
5           1.60 µm, Snow/Ice - Near IR
6           2.20 µm, Cloud Particle - Near IR
7           3.90 µm, Shortwave Window - IR
8           6.20 µm, Upper-Level Water Vapor - IR 
9           6.90 µm, Mid-Level Water Vapor - IR 
10          7.30 µm, Lower-level Water Vapor - IR
11          8.40 µm, Cloud Top - IR
12          9.60 µm, Ozone - IR
13          10.3 µm, Clean Longwave Window - IR
14          11.2 µm, Longwave Window - IR
15          12.3 µm, Dirty Longwave Window - IR
16          13.3 µm, CO2 Longwave - IR
```

#### Resolutions
```
FD (Full Disk)
Resolutions     Bands
339x339         all
678x678         all
1808x1808       all
5424x5424       all
10848x10848     GeoColor, 1-3, 5
21696x21696     2

CONUS (contiguous United States)
Resolutions     Bands
416x250         all
625x375         all
1250x750        all
2500x1500       all
5000x3000       GeoColor, 1-3, 5
10000x6000      2

SECTOR/* (Regional sector view)
Resolutions     Bands
300x300         all
600x600         all
1200x1200       GeoColor, 1-3, 5
```

#### Types
```
Type        Description
FD          Full Disk (Entire Western Hemisphere)
CONUS       contiguous United States
SECTOR/pnw  Regional sector view: Pacific Northwest
SECTOR/nr   Regional sector view: Northern Rockies
SECTOR/umv  Regional sector view: Upper Mississippi Valley
SECTOR/cgl  Regional sector view: Great Lakes
SECTOR/ne   Regional sector view: Northeast
SECTOR/psw  Regional sector view: Pacific Southwest
SECTOR/sr   Regional sector view: Southern Rockies
SECTOR/sp   Regional sector view: Southern Plains
SECTOR/smv  Regional sector view: Southern Mississippi Valley
SECTOR/se   Regional sector view: Southeast
```

## Timestamp explanation
The images are labeled with a timestamp from NOAA. The timestamps are in **UTC** time.
The format of these timestamps are `YYYYDDDHHMM`. `YYYY` is the 4 digit year, `DDD` is the
number of days that have past since the start of the year (Jan 1 is 001). `HHMM` is the hour and minute.

An example might be `20181132152`. This timestamp was from the year 2018, day #113 and at 21:52 UTC,
or April 23, 2018 at 21:52 UTC.


## License
This program is copyrighted to Will Bresnahan or n9Mtq4 under the MIT License. More info in the [LICENSE File](https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper/blob/master/LICENSE).

## Building from source
1. Clone or download the source code
2. Extract the code if you downloaded the zip file
3. cd to the directory with the code
4. Either run "./gradlew build" on unix systems or "gradlew.bat build" on windows
5. Your shiny new jar will be located in "build/libs/"


## Recommended ffmpeg options for timelapse
If you wish to make a timelapse from this programs output, here is an ffmpeg command that I
recommend (for 5424x5424px), change the bitrate (-b:v) as needed.

`ffmpeg -framerate 30 -pattern_type glob -i '*.jpg' -c:v libx264 -b:v 20MB -maxrate 20MB -bufsize 100MB video.mp4`
