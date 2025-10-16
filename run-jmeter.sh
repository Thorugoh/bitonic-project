#!/bin/bash

jmeter -n -t jmeter/bitonic_test_plan.jmx -l jmeter/results.jtl -e -o jmeter/report

