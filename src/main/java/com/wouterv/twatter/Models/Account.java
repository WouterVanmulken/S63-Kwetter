package com.wouterv.twatter.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wouter Vanmulken on 18-2-2017.
 */
@Entity
@XmlRootElement
@NamedQueries(value ={
        @NamedQuery(name = "accountdao.findByUserName", query = "SELECT a FROM Account a where a.userName = :userName"),
//        @NamedQuery(name = "accountdao.search", query = "SELECT a FROM Account a where a.userName like :name or a.firstName like :name or a.lastName like :name"),
        @NamedQuery(name = "accountdao.search", query = "SELECT a FROM Account a where a.userName like :name "),
        @NamedQuery(name = "accountdao.getFollowing", query = "SELECT a FROM Account a where :id in (select f.Id from a.following f)")}
)
public class Account extends TweeterModel {


    @Column(unique = true, nullable = false)
    private String userName;
    @Column(unique = true, nullable = false)
    private String email;
    private String bio;
    private String firstName;
    private String lastName;
    @Column(nullable = false)
    private String password;//TODO : temporary password

    @ManyToMany(mappedBy = "accounts")
    @JsonManagedReference
    private List<Type> groups;

//    @OneToMany(mappedBy = "postAccount", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "postAccount")
//    @JsonManagedReference
//    @JsonManagedReference(value = "postAccount")
    private List<Tweet> tweets;

    @OneToMany
    @JsonManagedReference
    private List<Account> following;

    @ManyToMany
    @JsonManagedReference
//    @JoinTable(name="ACC_MENTION",
//            joinColumns=@JoinColumn(name="ACCOUNT_ID"),
//            inverseJoinColumns=@JoinColumn(name="MENTION_ID"))
    private List<Tweet> mentions;


    public Account() {}

    public Account(String userName, String email, String bio, String firstName, String lastName) {

        this.userName = userName;
        this.email = email;
        this.bio = bio;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @XmlTransient
//    @JsonManagedReference()
    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }
//    @XmlTransient
    public List<Account> getFollowing() {
        if (following == null) {
            return new ArrayList<Account>();
        }
        return following;
    }

    public void setFollowing(List<Account> following) {
        this.following = following;
    }

    public void addFollowing(Account tofollow) {
        if (following == null) {//TODO : is this neccesary ?
            following = new ArrayList<>();
        }
        following.add(tofollow);
    }

    public void removeFollowing(Account toUnfollow) {
        if (following == null) {//TODO : is this neccesary ?
            following = new ArrayList<>();
        }
        following.remove(toUnfollow);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    @XmlTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @XmlTransient
    public List<Type> getGroups() {
        return groups;
    }

    public void setGroups(List<Type> groups) {
        this.groups = groups;
    }

    public void addGroup(Type type) {
        if (!groups.contains(type)) {
            groups.add(type);
        }
    }

    public void removeGroup(Type type) {
        if (groups.contains(type)) {
            groups.remove(type);
        }
    }
//    @XmlTransient
    public List<Tweet> getMentions() {
        return mentions;
    }

    public void setMentions(List<Tweet> mentions) {
        this.mentions = mentions;
    }


    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }
}
