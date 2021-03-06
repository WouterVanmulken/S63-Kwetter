package com.wouterv.twatter.Service;

import com.wouterv.twatter.Utils.JPA;
import com.wouterv.twatter.DAO.DAO.IAccountDAO;
import com.wouterv.twatter.DAO.DAO.IHashtagDAO;
import com.wouterv.twatter.DAO.DAO.ITweetDAO;
import com.wouterv.twatter.Event.LogEvent;
import com.wouterv.twatter.Interceptor.VolgTrendInterceptor;
import com.wouterv.twatter.Models.Account;
import com.wouterv.twatter.Models.Hashtag;
import com.wouterv.twatter.Models.Tweet;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wouter Vanmulken on 8-3-2017.
 */

@Stateless
public class TweetService {

    @Inject
    @JPA
    ITweetDAO tweetDAO;

    @Inject
    @JPA
    IAccountDAO accountDAO;

    @Inject
    @JPA
    IHashtagDAO hashtagDAO;

    @Inject
    @Any
    Event<LogEvent> logEvent;

    public Tweet findById(int id) {
        return tweetDAO.findById(id);
    }

    public List<Tweet> getAllTweets() {
        return tweetDAO.getAll();
    }

    public List<Tweet> postedTweets(int userId) {
        return tweetDAO.getPostedTweets(userId);
    }

    @Interceptors(VolgTrendInterceptor.class)
    public Tweet create(String content, int userId) {//TODO : remove the userId and use JAAS

        Account account = accountDAO.findById(userId);
        Tweet tweet = new Tweet(content, account);

        Pattern hashtagPattern = Pattern.compile("\\#\\w+");
        Matcher hm = hashtagPattern.matcher(content);
        while (hm.find()) {
            Hashtag hashtag = hashtagDAO.findOrCreate(hm.group());
//            tweet.addHashtag(hashtag);
            hashtag.addTweets(tweet);
        }
        Pattern mentionsPattern = Pattern.compile("\\@\\w+");
        Matcher mm = mentionsPattern.matcher(content);
        while (mm.find()) {
            String username = mm.group().substring(1);
            Account mentionAccount = accountDAO.findByUserName(username);
            tweet.addMention(mentionAccount);
        }
        tweetDAO.create(tweet);
        accountDAO.edit(account);
        logEvent.fire(new LogEvent());
        return tweet;
    }

    @Interceptors(VolgTrendInterceptor.class)
    public Tweet edit(int id, String content) {//TODO : remove the userId and use JAAS
        Tweet tweet = findById(id);
        tweet.setContent(content);
        tweetDAO.edit(tweet);
        return tweet;
    }

    public List<Tweet> getPersonalTweets(int Id) {
        return tweetDAO.getPersonalTweets(Id);
    }

    public List<Tweet> search(String content) {
        return tweetDAO.search(content);
    }

    public boolean remove(int id) {
        Tweet tweet = tweetDAO.findById(id);
        try {
            tweetDAO.remove(tweet);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean hearth(int tweetId, int userId) {
        try {
            Tweet tweet = tweetDAO.findById(tweetId);
            Account account = accountDAO.findById(userId);
            if (tweet == null || account == null) throw new Exception("Tweet or account not found");
            boolean success;
            if (!tweet.getHearted().contains(account)) {
                success = tweet.addHearted(account);
            } else {
                success = tweet.removeHearted(account);
            }
            tweetDAO.edit(tweet);
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    public void setTweetDAO(ITweetDAO tweetDAO) {
        this.tweetDAO = tweetDAO;
    }

    public void setAccountDAO(IAccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void  logCreateKwetter(@Observes LogEvent event){event.printLine("Created a tweet");}
}
