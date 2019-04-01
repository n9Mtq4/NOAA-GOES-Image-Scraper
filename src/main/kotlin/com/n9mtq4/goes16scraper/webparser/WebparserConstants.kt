package com.n9mtq4.goes16scraper.webparser

import com.n9mtq4.goes16scraper.ImageOptions

/**
 * Created by will on 4/25/18 at 4:07 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

const val ROOT_URL = "https://cdn.star.nesdis.noaa.gov/"
const val POST_ROOT_URL = "/ABI/"

const val USER_AGENT = "n9Mtq4-goes-east-scrapper/0.3 (+https://github.com/n9Mtq4/NOAA-Goes-16-image-scraper)"

fun getNoaaUrlStr(imageOptions: ImageOptions): String {
	return "$ROOT_URL/${imageOptions.satellite}/$POST_ROOT_URL${imageOptions.type}/${imageOptions.band}/"
}
