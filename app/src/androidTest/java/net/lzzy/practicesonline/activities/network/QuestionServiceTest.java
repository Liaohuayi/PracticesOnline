package net.lzzy.practicesonline.activities.network;

import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.view.QuestionType;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by lzzy_gxy on 2019/4/22.
 * Description:
 */
public class QuestionServiceTest {

    @Test
    public void textGetQuestionsOfParacticeFromServer() throws IOException {
        String s=QuestionService.getQuestionsOfParacticeFromServer(28);
       assertTrue(s.contains("交互性和充分性"));
    }

    @Test
    public void testGetQuestions() throws IOException, IllegalAccessException, JSONException, InstantiationException {
        String json = QuestionService.getQuestionsOfParacticeFromServer(28);
        List<Question> questions = QuestionService.getQuestion(json, UUID.randomUUID());
        Question question = questions.get(1);
        assertEquals(6, questions.size());
        assertTrue(question.getContent().contains("主要目的在于"));
        assertEquals(QuestionType.SINGLE_CHOICE, question.getType());
        assertEquals(4, question.getOptions().size());
        assertTrue(question.getOptions().get(0).isAnswer());
    }
}
