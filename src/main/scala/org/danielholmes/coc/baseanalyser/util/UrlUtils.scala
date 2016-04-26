package org.danielholmes.coc.baseanalyser.util

import com.google.common.net.UrlEscapers

object UrlUtils {
  def encodeFragment(part: String): String = {
    UrlEscapers.urlFragmentEscaper.escape(part)
  }
}
