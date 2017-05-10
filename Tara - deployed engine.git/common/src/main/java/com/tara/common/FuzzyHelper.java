package com.tara.common;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzyHelper implements FuzzyMatcher {

	public FuzzyHelper() {

	}

	public static void main(String args[]) {
		System.out.println(new FuzzyHelper().fuzzyScore("kaab bin tariq",
				"kaab"));
	}

	public Integer fuzzyScore(String term, String query) {
		return FuzzySearch.ratio(term, query);
	}
}
