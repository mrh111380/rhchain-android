package nantes_sqli.rhchain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by alb on 22/11/16. Votes for a specific survey
 *
 * class to import data store in BC Not working at this time Parameters: User's list who had
 * answered Vote, containing array per questions id's question and answer's value
 */

public class Results implements Parcelable {
    /**
     * the user who have answered
     */
    User user;
    /**
     * list of votes providing the identifiers of questions and answers
     */
    ArrayList<Vote> votes;
    /**
     * survey from which the results are display
     */
    Survey survey;

    Boolean isCompleted = Boolean.FALSE;

    public Results(ArrayList<Vote> votes) {
        this.votes = votes;
    }

    public Results() {
        this.votes = new ArrayList<Vote>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserById(String userId) {
        this.user = User.getUserById("demo");
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
    }


    public int nbVote() {
        int number = votes.size();
        return number;
    }

    public Boolean isCompleted(){
        if (nbVote() == survey.nbQuestion()) {
            isCompleted = Boolean.TRUE;
            return isCompleted;
        }
        return isCompleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.user);
        dest.writeList(this.votes);
        dest.writeParcelable(this.survey, flags);
        dest.writeValue(this.isCompleted);
    }

    protected Results(Parcel in) {
        this.user = (User) in.readSerializable();
        this.votes = new ArrayList<Vote>();
        in.readList(this.votes, Vote.class.getClassLoader());
        this.survey = in.readParcelable(Survey.class.getClassLoader());
        this.isCompleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Results> CREATOR = new Parcelable.Creator<Results>() {
        @Override
        public Results createFromParcel(Parcel source) {
            return new Results(source);
        }

        @Override
        public Results[] newArray(int size) {
            return new Results[size];
        }
    };
}
