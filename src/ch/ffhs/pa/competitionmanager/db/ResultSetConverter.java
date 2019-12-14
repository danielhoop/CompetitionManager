package ch.ffhs.pa.competitionmanager.db;

import ch.ffhs.pa.competitionmanager.db.Query;
import ch.ffhs.pa.competitionmanager.entities.Competitor;
import ch.ffhs.pa.competitionmanager.entities.Score;
import ch.ffhs.pa.competitionmanager.enums.Gender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class ResultSetConverter {

    public static Score toScore(ResultSet rs) throws SQLException {
        Time timeNeeded = rs.getTime("time_needed");
        LocalTime localTimeNeeded = null;
        if (timeNeeded != null) {
            int offsetInMinutes = timeNeeded.getTimezoneOffset();
            int numberOfHours = offsetInMinutes / 60;
            localTimeNeeded = timeNeeded.toLocalTime().plusHours(numberOfHours);
            long millis = (timeNeeded.getTime() % 1000);
            localTimeNeeded = localTimeNeeded.plusNanos(millis * 1000000);
        }

        /*if (rs.getString("name").startsWith("Marta")) {
            System.out.print("");
        }*/

        return new Score(
                rs.getLong("id"),
                rs.getLong("event_id"),
                new Competitor(
                        rs.getLong("competitor_id"),
                        rs.getString("name"),
                        Gender.valueOf(rs.getInt("gender")),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getInt(Query.ageColumnName(rs.getLong("event_id")))
                ),
                localTimeNeeded,
                rs.getDouble("points_achieved"),
                rs.getInt("number_of_tries"),
                rs.getBoolean("is_valid"),
                rs.getTimestamp("time_of_recording").toLocalDateTime(),
                rs.getBoolean("deleted")
        );
    }
}
