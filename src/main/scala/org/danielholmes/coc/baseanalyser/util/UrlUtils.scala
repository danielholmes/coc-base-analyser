package org.danielholmes.coc.baseanalyser.util

import java.net.URLEncoder

object UrlUtils {
  def encode(part: String): String = {
    URLEncoder.encode(part, "UTF-8")
      .replace("+", "%20")
  }
}
