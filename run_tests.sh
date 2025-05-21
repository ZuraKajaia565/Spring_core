#!/bin/bash
# run_tests.sh

mvn test -Dtest=IntegrationTestRunner

mvn test -Dtest=ComponentTestRunner


echo "Step 4: Run all JUnit tests"
mvn test -Dtest=com.zura.gymCRM.*Test 
echo "All tests completed"
