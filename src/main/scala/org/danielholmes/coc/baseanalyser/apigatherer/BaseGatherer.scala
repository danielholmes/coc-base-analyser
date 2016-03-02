package org.danielholmes.coc.baseanalyser.apigatherer

class BaseGatherer {
  def gather(userName: String) = {
    """
      |[{
      |  "data": 1000001,
      |  "lvl": 1,
      |  "x": 21,
      |  "y": 20
      |}, {
      |  "data": 1000004,
      |  "lvl": 0,
      |  "x": 20,
      |  "y": 16,
      |  "res_time": 8958
      |}, {
      |  "data": 1000000,
      |  "lvl": 0,
      |  "x": 26,
      |  "y": 19,
      |  "units": [
      |    [4000006, 2]
      |  ],
      |  "storage_type": 0
      |}, {
      |  "data": 1000015,
      |  "lvl": 0,
      |  "x": 18,
      |  "y": 20
      |}, {
      |  "data": 1000014,
      |  "lvl": 0,
      |  "locked": true,
      |  "x": 25,
      |  "y": 32
      |}, {
      |  "data": 1000008,
      |  "lvl": 0,
      |  "x": 23,
      |  "y": 24
      |}, {
      |  "data": 1000015,
      |  "lvl": 0,
      |  "x": 21,
      |  "y": 24
      |}, {
      |  "data": 1000002,
      |  "lvl": 0,
      |  "x": 26,
      |  "y": 25,
      |  "res_time": 8957
      |}, {
      |  "data": 1000003,
      |  "lvl": 0,
      |  "x": 23,
      |  "y": 27
      |}, {
      |  "data": 1000005,
      |  "lvl": 0,
      |  "x": 20,
      |  "y": 26
      |}, {
      |  "data": 1000006,
      |  "lvl": 0,
      |  "x": 18,
      |  "y": 23,
      |  "unit_prod": {
      |    "unit_type": 0
      |  }
      |}, {
      |  "data": 1000008,
      |  "lvl": -1,
      |  "const_t": 59,
      |  "const_t_end": 1416678146,
      |  "x": 17,
      |  "y": 26
      |}]
    """.stripMargin
  }
}
