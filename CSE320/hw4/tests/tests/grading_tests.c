#include <errno.h>
#include <stdio.h>
#include <sys/stat.h>
#include <criterion/criterion.h>

#include "__grading_helpers.h"

/* -------------------------------------------------------------------------- */
/* ------------------------------ Output Suite ------------------------------ */
/* -------------------------------------------------------------------------- */

/* Every test in this suite will run with real commands */

/* Supply the program with an invalid flag, program should fail */
Test(output_suite, invalid_args_test, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -a tests/nonexistent";

	int return_code = WEXITSTATUS(system(cmd));
	assert_failure(return_code);
}

/* Run with an invalid task, program should fail */
Test(output_suite, invalid_task_test, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/invalid_task.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_failure(return_code);
}

/* Simple test that prints a string to stdout */
Test(output_suite, welcome_test, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/welcome.ckb > tmp/welcome.out";
	char *cmp = "cmp tmp/welcome.out tests/rsrc/welcome.out";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* Single recipe with simple tasks with no piping */
Test(output_suite, no_piping_test, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/no_piping.ckb > tmp/no_piping.out";
	char *cmp = "cmp tmp/no_piping.out tests/rsrc/no_piping.out";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* Single recipe with simple tasks with piping */
Test(output_suite, piping_test, .init=mkdir_tmp, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/piping.ckb > tmp/piping.out";
	char *cmp = "cmp tmp/piping.out tests/rsrc/piping.out";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* Single recipe with simple tasks with input redirection */
Test(output_suite, input_redirection, .init=mkdir_tmp, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/input_redirection.ckb > tmp/input_redirection.out";
	char *cmp = "cmp tmp/input_redirection.out tests/rsrc/input_redirection.out";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* Single recipe with simple tasks with output redirection */
Test(output_suite, output_redirection, .init=mkdir_tmp, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/output_redirection.ckb";
	char *cmp = "cmp tmp/output_redirection.out tests/rsrc/output_redirection.out";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* -------------------------------------------------------------------------- */
/* ----------------------------- Piping Suite ------------------------------- */
/* -------------------------------------------------------------------------- */

/* Single recipe with simple tasks with no redirections */
Test(piping_suite, basic_tasks_no_pipeline_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_tasks_no_pipeline.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Single recipe with a single task that has multiple steps */
Test(piping_suite, basic_tasks_with_pipeline_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_tasks_with_pipeline.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Contains an invalid command, pipeline should fail */
Test(piping_suite, error_in_pipeline_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/error_in_pipeline.ckb -e";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Reading input file at the last step in a task, pipeline should fail */
Test(piping_suite, error_in_pipeline_no_input_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/error_in_pipeline_no_input.ckb -e";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Contains a command that returns an exit code of 1, pipeline should fail */
Test(piping_suite, basic_tasks_with_pipeline_nonzero_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_tasks_with_pipeline_nonzero.ckb -e";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Kill a task that is being processed, pipeline should fail */
Test(piping_suite, basic_tasks_with_pipeline_signal_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_tasks_with_pipeline_signal.ckb -e";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Piping with real commands */
Test(piping_suite, real_tasks_with_pipeline_outfile_test, .init=mkdir_tmp, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 1 -f tests/rsrc/real_tasks_with_pipeline_outfile.ckb";
	char *cmp = "cmp tmp/outfile tests/rsrc/real_tasks_with_pipeline_outfile.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* -------------------------------------------------------------------------- */
/* --------------------------- Dependency Suite ----------------------------- */
/* -------------------------------------------------------------------------- */

/* Every recipe has one sub-recipe */
Test(dependency_suite, basic_subrecipe_single_dep_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_subrecipe_single_dep.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* There is an unused recipe */
Test(dependency_suite, unused_recipe_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_extra_recipe.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Recipes have multiple sub-recipes */
Test(dependency_suite, basic_subrecipe_multiple_deps_seq_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_subrecipe_multiple_deps_seq.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* There is an invalid sub-recipe, program should fail */
Test(dependency_suite, invalid_subrecipe_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/invalid_subrecipe.ckb -e";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Every task has delay = 0, scheduler should start next task immediately */
Test(dependency_suite, basic_subrecipe_multiple_deps_nodelay, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 1 -f tests/rsrc/basic_subrecipe_multiple_deps_nodelay.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}


/* -------------------------------------------------------------------------- */
/* ----------------------------- Parallel Suite ----------------------------- */
/* -------------------------------------------------------------------------- */

/* Piping with real commands, multiple cooks */
Test(parallel_suite, parallel_real_tasks_with_pipeline_outfile_test, .init=mkdir_tmp, .timeout=20) {
	char *cmd = "ulimit -t 10; bin/cook -c 2 -f tests/rsrc/real_tasks_with_pipeline_outfile.ckb";
	char *cmp = "cmp tmp/outfile tests/rsrc/real_tasks_with_pipeline_outfile.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
	return_code = WEXITSTATUS(system(cmp));
	assert_output_matches(return_code);
}

/* Every task has delay = 0, scheduler should start next task immediately, multiple cooks */
Test(parallel_suite, parallel_basic_subrecipe_multiple_deps_nodelay, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 3 -f tests/rsrc/basic_subrecipe_multiple_deps_nodelay.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Recipes with multiple sub-recipes, run with multiple cooks */
Test(parallel_suite, parallel_subrecipe_multiple_deps_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 3 -f tests/rsrc/basic_subrecipe_multiple_deps_seq.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Recipe having larger number of parallel tasks compared to max_cook parallel recipes/tasks */
Test(parallel_suite, more_than_max_cook_parallelism_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 2 -f tests/rsrc/more_than_max_cook_parallelism.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Recipe having larger number of parallel tasks compared to max_cook parallel recipes/tasks, run with no delays */
Test(parallel_suite, more_than_max_cook_parallelism_nodelay, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 2 -f tests/rsrc/more_than_max_cook_parallelism_nodelay.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Many tasks can run in parallel */
Test(parallel_suite, large_parallelism_test, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 10 -f tests/rsrc/large_parallelism.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}

/* Many tasks can run in parallel, no delays */
Test(parallel_suite, large_parallelism_nodelay, .timeout=20) {
	char *cmd = "ulimit -t 10; python3 tests/test_cook.py -c 10 -f tests/rsrc/large_parallelism_nodelay.ckb";

	int return_code = WEXITSTATUS(system(cmd));
	assert_success(return_code);
}
