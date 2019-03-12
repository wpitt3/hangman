package dulcinea

class EsAggAnalyser {

  static Map groupAggsByLetter(List aggs, List ignoreLetters, List previousKeys = []) {
    return aggs.findAll{!previousKeys.contains(it.key)}.collectEntries{
      [(it.key): it.aggs ? groupAggsByLetter(it.aggs, ignoreLetters, previousKeys.clone() + it.key) : it.count]
    }.groupBy{it.key[0]}
        .findAll{!ignoreLetters.contains(it.key)}
  }

  static Integer findMinScore(Map groups) {
    max(groups).min{it.value}.value
  }

  static Map.Entry findValueForMinScore(Map groups) {
    max(groups).min{it.value}
  }

  static Map findValuesForMinScore(Map groups) {
    max(groups)
  }

  static Map processGroups2(Map groups) {
    groups.collectEntries{ k1, v1 ->
      [(k1) : min(v1)]
    }
  }

  private static Map min(Map groups) {
    return groups.collectEntries{ k, v ->
      [(k): (v instanceof Map ? max(v).min{it.value}.value : v)]
    }
  }

  private static Map max(Map groups) {
    return groups.collectEntries{ k, v ->
      [(k): (v instanceof Map ? min(v).max{it.value}.value : v)]
    }
  }
}
