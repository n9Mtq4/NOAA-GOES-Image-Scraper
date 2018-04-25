package com.n9mtq4.goes16scraper.webparser

import com.n9mtq4.goes16scraper.ImageOptions
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup

/**
 * Created by will on 4/25/18 at 3:25 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

/**
 * Gets a list of images from the image catalog
 * */
internal fun parseCatalog(imageOptions: ImageOptions): List<Pair<String, String>> {
	
	val urlStr = "$ROOT_URL${imageOptions.type}/${imageOptions.band}/"
	val jsonUrl = urlStr + "catalog.json"
	println(jsonUrl)
	
	val jsonStr = Jsoup
			.connect(jsonUrl)
			.header("Accept-Encoding", "gzip, deflate, br")
			.userAgent(USER_AGENT)
			.ignoreContentType(true)
			.timeout(3000)
			.maxBodySize(0).ignoreHttpErrors(true).followRedirects(true)
			.execute()
			.body()
	
	val parser = JSONParser()
	val json: JSONObject = parser.parse(jsonStr) as JSONObject
	val images = json["images"] as JSONObject
	val imageList = (images[imageOptions.res] as JSONArray).toList().map { it as String }
	val imageUrlList = imageList.map { it to urlStr + it }
	
	return imageUrlList
	
}
