package app.habit.service.gpt.coach;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class EvaluationGptCoachTest {

    @Autowired
    private EvaluationGptCoach evaluationGptCoach;
    private final String testUrl = "https://api.openai.com/v1/chat/completions";

    @Test
    @DisplayName("gpt 에게 프롬프트를 보내 사용자의 현재 습관 형성 단계를 받아온다.")
    void send_prompt_to_gpt() {
        // given
        String prompt = """
                You are a habit formation expert.
                Below are user responses to the questionnaire you created to evaluate the habit formation stages of others.
                Please use the Transtheoretical Model (TTM) as an evaluation standard!
                You have an accurate understanding of the characteristics of each stage of TTM, and you can accurately evaluate the user's current habit formation stage by analyzing the user's answers from five perspectives.

                ## 1. General awareness and intention
                Question: What habits are you currently working on developing and why do you think they are important?
                Answer: "I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible."

                ## 2. Goal clarity and planning
                Question: Have you set specific goals related to this habit? What steps have you planned to achieve these goals?
                Answer: "I set a specific goal to journal for at least 10 minutes every night before going to bed. My plan includes setting a recurring reminder on my phone and keeping a journal and pen on my bedside table to minimize barriers to this habit. It's possible."

                ## 3. Initial actions and small steps
                Question: What is the smallest step you have taken or plan to take to start this habit?
                Answer: "The smallest step I took was to buy a journal I liked and write just a few sentences about how I felt that day. It made the process feel more engaging and less daunting."

                ## 4. Consistency and practice
                How consistently have you been able to perform this habit and how do you monitor your progress?
                Answer: "I've been journaling for several days in a row now. To monitor my progress, I've created a habit tracker inside my diary where I check off the days I wrote each day. It's satisfying to see the streak going and keeps me motivated to keep going. "

                ## 5. Maintenance and lifestyle integration
                Question: Have you ever had trouble maintaining this habit over time? How did you deal with them?
                Answer: "There were a few nights when I didn't have the energy to write. On those nights, I wrote a few words the next morning, acknowledged the missed sessions, but focused on not feeling disconnected again. Flexibility and self-compassion helped me integrate this habit into my life. “I realized this was the key.”

                Looking at the above, I would like you to tell me which stage the user is in according to the habit formation model and provide an explanation of that stage.
                Evaluate the user’s habit formation stage in one of the five categories below!

                1. Precontemplation stage
                2. Consideration stage
                3. Preparation stage
                4. Action stage
                5. Maintenance stage

                The format of your answer is very important, so when you respond to me, please be sure to provide it in the same format as below!

                {
                "phaseType" : "consideration stage",
                "phaseDescription" : "In the consideration phase of goal setting and planning, the individual is transitioning from simply thinking about making a change or developing a new habit to actively planning how to do it. This phase may involve vague ideas or It involves crystallizing your desires into specific, actionable goals – moving from “I want to make a difference” to “Here’s exactly what I’m going to do and how.” The focus is on specificity, feasibility, and the journey ahead. “There are plans for this.”"
                }""";

        RequestPrompt requestPrompt = new RequestPrompt("gpt-3.5-turbo-0125",
                List.of(new PromptMessage(PromptRole.USER, prompt)));

        // when
        CompletableFuture<PhaseEvaluationRs> result = evaluationGptCoach.requestEvaluation(
                requestPrompt,
                testUrl);

        // then
        StepVerifier.create(Mono.fromFuture(result))
                .expectSubscription()
                .assertNext(phaseEvaluationRs -> {
                    assertThat(phaseEvaluationRs).isNotNull();
                    assertThat(phaseEvaluationRs.getPhaseType()).isNotNull();
                    assertThat(phaseEvaluationRs.getPhaseDescription()).isNotNull();
                })
                .verifyComplete();
    }
}
