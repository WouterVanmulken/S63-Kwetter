package com.wouterv.twatter.Service;

import com.wouterv.twatter.Annotations.JPA;
import com.wouterv.twatter.DAO.IAccountDAO;
import com.wouterv.twatter.DAO.ITweetDAO;
import com.wouterv.twatter.Models.Account;
import com.wouterv.twatter.Models.Tweet;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

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

    public List<Tweet> getAllTweets() {
        return tweetDAO.getAll();
    }

    public List<Tweet> postedTweets(int userId) {
        return tweetDAO.getPostedTweets(userId);
    }

    public Tweet create(String content, int userId) {//TODO : remove the userId and use JAAS
        Account account = accountDAO.findById(userId);
        Tweet tweet = new Tweet(content, account);
        tweetDAO.create(tweet);
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
}
