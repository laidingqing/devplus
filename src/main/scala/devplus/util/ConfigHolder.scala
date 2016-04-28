package com.devplus.util

import com.typesafe.config.ConfigFactory

trait ConfigHolder {
  val config = ConfigFactory.load()
}
