#include <criterion/criterion.h>
#include <criterion/logging.h>

#include "mtft.h"

static char *progname = "bin/mtft";

Test(basecode_tests_suite, validargs_help_test, .timeout=5) {
    char *argv[] = {progname, "-h", NULL};
    int argc = (sizeof(argv) / sizeof(char *)) - 1;
    int ret = validargs(argc, argv);
    int exp_ret = 0;
    int opt = global_options;
    int flag = 0x80000000;
    cr_assert_eq(ret, exp_ret, "Invalid return for validargs.  Got: %d | Expected: %d",
		 ret, exp_ret);
    cr_assert_eq(opt & flag, flag, "Correct bit (0x%x) not set for -h. Got: %x",
		 flag, opt);
}

Test(basecode_tests_suite, validargs_encode_test, .timeout=5) {
    char *argv[] = {progname, "-e", NULL};
    int argc = (sizeof(argv) / sizeof(char *)) - 1;
    int ret = validargs(argc, argv);
    int exp_ret = 0;
    int opt = global_options;
    int exp_opt = 0x40000001;
    cr_assert_eq(ret, exp_ret, "Invalid return for validargs.  Got: %d | Expected: %d",
		 ret, exp_ret);
    cr_assert_eq(opt, exp_opt, "Invalid options settings.  Got: 0x%x | Expected: 0x%x",
		 opt, exp_opt);
}

Test(basecode_tests_suite, validargs_decode_test, .timeout=5) {
    char *argv[] = {progname, "-d", NULL};
    int argc = (sizeof(argv) / sizeof(char *)) - 1;
    int ret = validargs(argc, argv);
    int exp_ret = 0;
    int opt = global_options;
    int exp_opt = 0x20000001;
    cr_assert_eq(ret, exp_ret, "Invalid return for validargs.  Got: %d | Expected: %d",
		 ret, exp_ret);
    cr_assert_eq(opt, exp_opt, "Invalid options settings.  Got: 0x%x | Expected: 0x%x",
		 opt, exp_opt);
}

Test(basecode_tests_suite, validargs_bits_test, .timeout=5) {
    char *argv[] = {progname, "-e", "-b", "02", NULL};
    int argc = (sizeof(argv) / sizeof(char *)) - 1;
    int ret = validargs(argc, argv);
    int exp_ret = 0;
    int opt = global_options;
    int exp_opt = 0x40000002;
    cr_assert_eq(ret, exp_ret, "Invalid return for validargs.  Got: %d | Expected: %d",
		 ret, exp_ret);
    cr_assert_eq(opt, exp_opt, "Invalid options settings.  Got: 0x%x | Expected: 0x%x",
		 opt, exp_opt);
}

Test(basecode_tests_suite, validargs_error_test, .timeout=5) {
    char *argv[] = {progname, "-d", "-b", NULL};
    int argc = (sizeof(argv) / sizeof(char *)) - 1;
    int exp_ret = -1;
    int ret = validargs(argc, argv);
    cr_assert_eq(ret, exp_ret, "Invalid return for validargs.  Got: %d | Expected: %d",
		 ret, exp_ret);
}

Test(basecode_tests_suite, help_system_test, .timeout=5) {
    char *cmd = "bin/mtft -h > /dev/null 2>&1";

    // system is a syscall defined in stdlib.h
    // it takes a shell command as a string and runs it
    // we use WEXITSTATUS to get the return code from the run
    // use 'man 3 system' to find out more
    int return_code = WEXITSTATUS(system(cmd));

    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
		 return_code);
}

Test(basecode_tests_suite, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -e < rsrc/banana.txt > test_output/banana.out";
    char *cmp = "cmp test_output/banana.out rsrc/banana.out";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
		 return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}

Test(m, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -e < rsrc/gettysburg.txt > test_output/gettysburg.out";
    char *cmp = "cmp test_output/gettysburg.out rsrc/gettysburg.out";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}

Test(bisonChamp, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -e < rsrc/bison > test_output/bison.out";
    char *cmp = "cmp test_output/bison.out rsrc/bison.out";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}

//1 byte data decoding
Test(decodeBanana, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -d < rsrc/banana.out > test_output/banana.txt";
    char *cmp = "cmp test_output/banana.txt rsrc/banana.txt";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}


Test(decodeLincoln, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -d < rsrc/gettysburg.out > test_output/gettysburg.txt";
    char *cmp = "cmp test_output/gettysburg.txt rsrc/gettysburg.txt";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}


Test(decodeBison, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -d < rsrc/bison.out > test_output/bison";
    char *cmp = "cmp test_output/bison rsrc/bison";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}


Test(ed_bison, mtft_basic_test, .timeout=5) {
    char *cmd = "bin/mtft -e -b 2 < rsrc/bison | bin/mtft -d -b 2 > test_output/bison2";
    char *cmp = "cmp test_output/bison2 rsrc/bison";

    int return_code = WEXITSTATUS(system(cmd));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program exited with %d instead of EXIT_SUCCESS",
         return_code);
    return_code = WEXITSTATUS(system(cmp));
    cr_assert_eq(return_code, EXIT_SUCCESS,
                 "Program output did not match reference output.");
}