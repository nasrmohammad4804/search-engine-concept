
## query i used for webpage suggestion .
## i can use only multi_match but with this query result is more precise.
## at first find title is exact match and distance between each them fewer than 3 word
## after that . if next step it found with fuzzy operation. if we use only multi_match for some it found term with larger distance or maybe has typo error. and
## this is not out prefer at first level

        GET /webpages/_search
        {
            "query": {
                "bool": {
                    "should": [
                        {
                            "match_phrase_prefix": {
                                "title": {
                                    "query": "kebas z",
                                    "_name":"phrase",
                                    "slop":3,
                                    "boost":2
                                }
                            }
                        },
                    {
                        "multi_match": {
                            "query": "kebas z",
                            "type": "bool_prefix",
                            "fields": ["title"],
                            "fuzziness": "2",
                            "boost": 1,
                            "operator": "and",
                            "_name":"multi_match"
                        }
                    }
                ],
                "minimum_should_match": 1
            }
        },
        "highlight": {
            "fields": {
                "title":{
                "pre_tags": "<em>",
                "post_tags": "</em>",
                "number_of_fragments": 1,
                "fragment_size": 150
                }   
            }
        }
    }