package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HabitAssessmentManagerTest {

    @Test
    @DisplayName("subject들로 gpt에게 보낼 프롬프트를 생성한다.")
    void create_prompt() {
        // given
        HabitAssessmentManager given = createHabitAssessmentManager();

        // when
        String result = given.createPrompt();

        // then
        assertThat(result).isEqualTo("""
                ## 1. General awareness and intention
                Question : What habits are you currently working on developing and why do you think they are important?
                Answer : I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible.

                ## 2. Goal clarity and planning
                Question : Have you set specific goals related to this habit? What steps have you planned to achieve these goals?
                Answer : I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible.

                ## 3. Initial actions and small steps
                Question : What is the smallest step you have taken or plan to take to start this habit?
                Answer : The smallest step I took was to buy a journal I liked and write just a few sentences about how I felt that day. It made the process feel more engaging and less daunting.
                
                """);
    }

    private HabitAssessmentManager createHabitAssessmentManager() {
        return new HabitAssessmentManager(
                List.of(
                        new Subject("1",
                                "General awareness and intention",
                                new Question("1",
                                        "What habits are you currently working on developing and why do you think they are important?"),
                                new Answer("1",
                                        "I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible.")),
                        new Subject("2",
                                "Goal clarity and planning",
                                new Question("2",
                                        "Have you set specific goals related to this habit? What steps have you planned to achieve these goals?"),
                                new Answer("3",
                                        "I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible.")
                        ),
                        new Subject("3",
                                "Initial actions and small steps",
                                new Question("3",
                                        "What is the smallest step you have taken or plan to take to start this habit?"),
                                new Answer("3",
                                        "The smallest step I took was to buy a journal I liked and write just a few sentences about how I felt that day. It made the process feel more engaging and less daunting.")
                        )));
    }
}
