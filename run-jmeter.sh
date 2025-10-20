#!/bin/bash
rm -rf jmeter/report
rm -f jmeter/results.jtl
jmeter -n -t jmeter/bitonic_test_plan.jmx -l jmeter/results.jtl -e -o jmeter/report

