package com.brocktek.farm.errors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.brocktek.problem.Problem;
import com.brocktek.problem.ProblemService;
import com.google.inject.Singleton;

@Singleton
public class SimpleProblemService implements ProblemService {
	private ObservableList<Problem> problemList;

	@Override
	public void start() {
		problemList = FXCollections.observableArrayList();
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return problemList;
	}

	@Override
	public void putProblem(Problem problem) {
		problemList.add(problem);
	}

	@Override
	public void clearProblem(Problem problem) {
		problemList.remove(problem);
	}
}
