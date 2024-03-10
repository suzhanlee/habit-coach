package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EvaluationPromptFactoryTest {

    @Test
    @DisplayName("gpt에게 보낼 프롬프트 dto를 생성한다.")
    void create_request_prompt() {
        // given
        EvaluationPromptFactory factory = new EvaluationPromptFactory();
        HabitFormingPhase phase = createHabitFormingPhase();

        // when
        RequestPrompt result = factory.create(phase);

        // then
        assertThat(result).isEqualTo(createExpected());
    }

    private HabitFormingPhase createHabitFormingPhase() {
        return new HabitFormingPhase(new HabitAssessmentManager(List.of(
                new Subject("1", "주제1", new Question("1", "질문1"), new Answer("1", "대답1")),
                new Subject("2", "주제2", new Question("2", "질문2"), new Answer("2", "대답2")),
                new Subject("3", "주제3", new Question("3", "질문3"), new Answer("3", "대답3"))
        )));
    }

    private RequestPrompt createExpected() {
        return new RequestPrompt("gpt-3.5-turbo-0125", List.of(new PromptMessage(
                PromptRole.USER, """
                You are a habit formation expert.
                Below are user responses to the questionnaire you created to evaluate the habit formation stages of others.
                Please use the Transtheoretical Model (TTM) as an evaluation standard!
                You have an accurate understanding of the characteristics of each stage of TTM, and you can accurately evaluate the user's current habit formation stage by analyzing the user's answers from five perspectives.

                ## 1. 주제1
                Question : 질문1
                Answer : 대답1

                ## 2. 주제2
                Question : 질문2
                Answer : 대답2

                ## 3. 주제3
                Question : 질문3
                Answer : 대답3

                Looking at the above, I would like you to tell me which stage the user is in according to the habit formation model and provide an explanation of that stage.
                Evaluate the user’s habit formation stage in one of the five categories below!

                1. Precontemplation stage
                2. Consideration stage
                3. Preparation stage
                4.Action stage
                5.Maintenance stage

                The format of your answer is very important, so when you respond to me, please be sure to provide it in the same format as below!

                {
                "phaseType" : "consideration stage",
                "phaseDescription" : "In the consideration phase of goal setting and planning, the individual is transitioning from simply thinking about making a change or developing a new habit to actively planning how to do it. This phase may involve vague ideas or It involves crystallizing your desires into specific, actionable goals – moving from “I want to make a difference” to “Here’s exactly what I’m going to do and how.” The focus is on specificity, feasibility, and the journey ahead. “There are plans for this.”"
                }At this time, a very important point is that the values of phaseType and phaseDescription should be of String type.Please write the phase description in less than 300 characters"""
        )));
    }
}
