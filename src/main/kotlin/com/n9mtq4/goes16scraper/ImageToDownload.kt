package com.n9mtq4.goes16scraper

/**
 * Created by will on 7/7/18 at 10:19 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

typealias ImageToDownloadList = List<ImageToDownload>

data class ImageToDownload(val imageName: String, val imageUrl: String, val imageSize: Long = -1)
