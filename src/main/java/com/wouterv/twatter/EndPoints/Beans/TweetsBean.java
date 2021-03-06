package com.wouterv.twatter.EndPoints.Beans;

import com.wouterv.twatter.Models.Account;
import com.wouterv.twatter.Models.Tweet;
import com.wouterv.twatter.Service.AccountService;
import com.wouterv.twatter.Service.TweetService;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Wouter Vanmulken on 21-3-2017.
 */
@Named
@SessionScoped
public class TweetsBean implements Serializable {
    @Inject
    TweetService service;
    @Inject
    AccountService accountService;


    public String create(Account currentUser) {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String newContent = params.get("createF:Content");
        service.create(newContent, currentUser.getId());
        return "app/index";
    }

    public List<Tweet> getAll() {
        return service.getAllTweets();
    }

    public List<Tweet> getPersonal(Account currentUser) {
        return service.getPersonalTweets(currentUser.getId());
    }

    public Tweet Heart(Tweet tweet, Account account) {
        service.hearth(tweet.getId(), account.getId());
        return tweet;
    }

    public boolean hasHearted(Tweet tweet, Account loggedIn) {
        for (Account a : tweet.getHearted()) {
            if (a.getId() == loggedIn.getId()) {
                return true;
            }
        }
        return false;
    }

}
