package websiteschema.mpsegment.web.api.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import websiteschema.mpsegment.web.UsingFixtures;
import websiteschema.mpsegment.web.api.model.*;
import websiteschema.mpsegment.web.ui.model.User;
import websiteschema.mpsegment.web.ui.service.UserService;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WordItemServiceTest extends UsingFixtures {

    private WordItemService wordItemService = resolve("wordItemServiceImpl", WordItemService.class);
    private ConceptService conceptService = resolve("conceptServiceImpl", ConceptService.class);
    private UserService userService = resolve("userServiceImpl", UserService.class);
    private String currentUserEmail = uniq("yingrui.f@gmail.com");

    @Before
    public void onSetUp() {
        setUpCurrentUser();
    }

    private void setUpCurrentUser() {
        User user = new User();
        user.setEmail(currentUserEmail);
        userService.addUser(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(currentUserEmail);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void should_add_word_item_in_database() {
        String wordName = uniq("WordName");

        WordItem wordItem = addWord(wordName, "pinyin", posN);

        WordItem wordItemInDatabase = wordItemService.getById(wordItem.getId());
        assertNotNull(wordItemInDatabase);
        assertEquals(wordName, wordItemInDatabase.getName());
        assertEquals(1, wordItemInDatabase.getPinyinSet().size());
        assertEquals("pinyin", wordItemInDatabase.getPinyinSet().iterator().next().getName());
    }

    @Test
    public void should_add_current_user_as_creator_of_word() {
        String wordName = uniq("WordName");

        WordItem wordItem = addWord(wordName, "pinyin", posN);

        WordItem wordItemInDatabase = wordItemService.getById(wordItem.getId());
        assertNotNull(wordItemInDatabase);
        User user = wordItemInDatabase.getUser();
        assertEquals(currentUserEmail, user.getEmail());
    }

    @Test
    public void should_add_part_of_speech_to_word() {
        String wordName = uniq("WordName");

        WordItem wordItem = new WordItem();
        wordItem.setName(wordName);

        Pinyin pinyin = new Pinyin();
        pinyin.setName("pinyin");

        wordItem.getPinyinSet().add(pinyin);
        WordFreq wordPosN = new WordFreq();
        wordPosN.setFreq(10);
        wordPosN.setPartOfSpeech(posN);
        wordItem.getWordFreqSet().add(wordPosN);
        WordFreq wordPosT = new WordFreq();
        wordPosT.setFreq(10);
        wordPosT.setPartOfSpeech(posT);
        wordItem.getWordFreqSet().add(wordPosT);
        wordItemService.add(wordItem);

        WordItem wordItemInDatabase = wordItemService.getById(wordItem.getId());
        assertNotNull(wordItemInDatabase);
        assertEquals(2, wordItemInDatabase.getWordFreqSet().size());
        Iterator<WordFreq> iterator = wordItemInDatabase.getWordFreqSet().iterator();
        String actualPos = iterator.next().getPartOfSpeech().getName() + iterator.next().getPartOfSpeech().getName();
        assertTrue(actualPos.contains("N"));
        assertTrue(actualPos.contains("T"));
    }

    @Test
    public void should_add_concepts_to_word() {
        String wordName = uniq("WordName");
        String c = uniq("Concept");

        WordItem wordItem = new WordItem();
        wordItem.setName(wordName);

        Concept concept = addConcept(c, posN, null);
        wordItem.getConceptSet().add(concept);

        wordItemService.add(wordItem);

        WordItem wordItemInDatabase = wordItemService.getById(wordItem.getId());
        assertNotNull(wordItemInDatabase);
        assertFalse(wordItemInDatabase.getConceptSet().isEmpty());
        assertEquals(c, wordItemInDatabase.getConceptSet().iterator().next().getName());
    }

    @Test
    public void should_find_all_words_by_pinyin() {
        String wordName1 = uniq("啊");
        String wordName2 = uniq("阿");
        String wordName3 = uniq("ai1");
        String pinyinA = uniq("a");

        WordItem wordItem1 = addWord(wordName1, pinyinA, posN, posT);
        WordItem wordItem2 = addWord(wordName2, pinyinA, posN, posT);
        WordItem wordItem3 = addWord(wordName3, "ai", posN, posT);

        List<WordItem> wordItems = wordItemService.findAllByPinyin(pinyinA);
        assertNotNull(wordItems);
        assertEquals(2, wordItems.size());
        Pinyin actualPinyin = wordItems.get(0).getPinyinSet().iterator().next();
        assertEquals(pinyinA, actualPinyin.getName());

        assertEquals(2, wordItems.get(0).getWordFreqSet().size());
    }

    @Test
    public void should_find_all_words() {
        String wordName1 = uniq("啊");
        String wordName2 = uniq("阿");
        String wordName3 = uniq("ai1");
        String pinyinA = uniq("a");

        WordItem wordItem1 = addWord(wordName1, pinyinA, posN);
        WordItem wordItem2 = addWord(wordName2, pinyinA, posN);
        WordItem wordItem3 = addWord(wordName3, "ai", posN);

        List<WordItem> wordItems = wordItemService.list();
        assertNotNull(wordItems);
        assertTrue(3 <= wordItems.size());
    }

    @Test
    public void should_find_all_words_begin_with_same_letter() {
        String wordName1 = uniq("啊");
        String wordName2 = uniq("阿");
        String wordName3 = uniq("ai1");
        String pinyinA = uniq("a");

        WordItem wordItem1 = addWord(wordName1, pinyinA, posN, posT);
        WordItem wordItem2 = addWord(wordName2, pinyinA, posN, posT);
        WordItem wordItem3 = addWord(wordName3, "ai", posN);

        List<WordItem> wordItems = wordItemService.findAllByWordHead(wordName1);
        assertNotNull(wordItems);
        assertEquals(wordName1, wordItems.get(0).getName());
    }

    private WordItem addWord(String wordName, String pinyin, PartOfSpeech... pos) {
        WordItem wordItem = new WordItem();
        wordItem.setName(wordName);

        Pinyin p = new Pinyin();
        p.setName(pinyin);
        wordItem.getPinyinSet().add(p);

        for (PartOfSpeech partOfSpeech : pos) {
            WordFreq wordFreq = new WordFreq();
            wordFreq.setFreq(10);
            wordFreq.setPartOfSpeech(partOfSpeech);
            wordItem.getWordFreqSet().add(wordFreq);
        }

        wordItemService.add(wordItem);
        return wordItem;
    }

    private Concept addConcept(String c, PartOfSpeech pos, Concept parent) {
        Concept concept = new Concept();
        concept.setName(c);
        concept.setNote(uniq("Note"));
        concept.setPartOfSpeech(pos);
        concept.setParent(parent);
        conceptService.add(concept);
        return concept;
    }

}
