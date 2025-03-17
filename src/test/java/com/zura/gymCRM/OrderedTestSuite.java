package com.zura.gymCRM;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        TrainerControllerTest.class,
        TraineeControllerTest.class,
        LoginControllerTests.class,
        TrainingControllerTests.class,
        TrainingTypeControllerTests.class

})
@TestMethodOrder(OrderAnnotation.class)
public class OrderedTestSuite {
}
