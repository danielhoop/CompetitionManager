package ch.ffhs.pa.competitionmanager.db;

import ch.ffhs.pa.competitionmanager.enums.Gender;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Contains strings for querying the database.
 */
public class Query {

    static String queryAllScoresForEvent(long event, boolean validOnly) {
        return "";
    }
    static String queryAllScoresForCategory(long event, long category, boolean validOnly) {
        return "";
    }
    static String queryAllScoresForCompetitor(long event, long competitor, boolean validOnly) {
        return "";
    }

    /**
     * Give back an SQL string to get the row from 'event' table that has specific id.
     * @return An SQL string to get the row from 'event' table that has specific id.
     */
    public static String eventById(long id) {
        return "select *\n" +
                "from `CompetitionManager`.`event`\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Give back an SQL string that will drop the column called 'age_<eventId>' in table 'competitor'.
     * @param eventId The id of the event.
     * @return An SQL string that will drop the column called 'age_<eventId>' in table 'competitor'.
     */
    public static String dropAgeColumn(long eventId) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "DROP COLUMN `" + ageColumnName(eventId) + "`;";
    }

    /**
     * Give back an SQL string that wil create an age column in table 'competitor' which contains the age in years.
     * @param eventId The id of the event.
     * @param dateOfToday A date string ISO-8601 format uuuu-MM-dd, like '2019-01-01'.
     * @return An SQL string that wil create an age column in table 'competitor' which contains the age in years.
     */
    static String createAgeColumn(long eventId, String dateOfToday) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "ADD COLUMN `" + ageColumnName(eventId) + "` TINYINT unsigned\n" +
                "AS (\n" +
                "    YEAR('" + dateOfToday + "') -\n" +
                "    YEAR(`date_of_birth`) - \n" +
                "    IF(STR_TO_DATE(CONCAT(YEAR('" + dateOfToday + "'), '-', MONTH(`date_of_birth`), '-', DAY(`date_of_birth`)) ,'%Y-%c-%e') > '" + dateOfToday + "', 1, 0)\n" +
                ");";
    }

    /**
     * Given an event id, returns an SQL string that will count the number of valid scores in table 'score'.
     * @param event The id of the event.
     * @return An SQL string that will count the number of valid scores in table 'score'.
     */
    public static String numberOfValidScores(long event) {
        return "select sum(`is_valid`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `event_id` = " + event + ";";
    }

    /**
     * Given an event id, returns an SQL string that will return the maximum id in table 'score'.
     * @param event The id of the event.
     * @return An SQL string that will return the maximum id in table 'score'
     */
    public static String maxIdInScores(long event) {
        return "select max(`id`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `is_valid` = true\n" +
                "  and `event_id` = " + event + ";";
    }

    public static String getScoresForCategory(long eventId, String viewName, boolean isTimeRelevant) {
        String queryPart1 = "SELECT s1.*\n" +
                "FROM `CompetitionManager`.`" + viewName + "` s1\n" +
                "LEFT JOIN `CompetitionManager`.`" + viewName + "` s2\n";

        String queryPart2;
        if (isTimeRelevant) {
            queryPart2 = "ON s1.`competitor_id` = s2.`competitor_id` AND timediff(s1.`time_needed`, s2.`time_needed`) > 0\n" +
                    "WHERE s2.`time_needed` IS NULL\n" +
                    "ORDER BY s1.`time_needed` ASC;";
        } else {
            queryPart2 = "ON s1.`competitor_id` = s2.`competitor_id` AND s1.`points_achieved` > s2.`points_achieved`\n" +
                    "WHERE s2.`points_achieved` IS NULL\n" +
                    "ORDER BY s1.`points_achieved` DESC;";
        }
        return queryPart1 + queryPart2;
    }

    public static String ageColumnName(long eventId) {
        return "age_" + eventId;
    }

    // --------------------------------------------
    // Methods to create SQL strings for categories
    // --------------------------------------------
    public static String getAllCategories(long eventId) {
        return "select *\n" +
                "    from `CompetitionManager`.`category`\n" +
                "    where `event_id` = " + eventId + ";";
    }
    public static String getAllViewNames() {
        return "SHOW FULL TABLES IN `CompetitionManager` WHERE TABLE_TYPE LIKE '%VIEW%';";
    }
    public static String categoryViewEventName(long eventId) {
        return "scores_category_" + eventId;
    }
    public static String categoryViewFullName(long eventId, long categoryId) {
        return "scores_category_" + eventId + "_" + categoryId;
    }
    public static String dropView(String viewName) {
        return "drop view `CompetitionManager`.`" + viewName + "`;";
    }
    public static String createScoreViewForCategory(long eventId, String viewName, int minAgeInclusive, int maxAgeInclusive, int gender) {
        return "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`gender`, c.`date_of_birth`, c.`" + ageColumnName(eventId) + "`\n" +
                "    from `CompetitionManager`.`score` s\n" +
                "    left join `CompetitionManager`.`competitor` c\n" +
                "        on s.`competitor_id` = c.`id`\n" +
                "    where     s.`deleted` = false\n" +
                "          AND s.`is_valid` = true\n" +
                "          AND s.`event_id` = "+ eventId + "\n" +
                "          AND c.`" + ageColumnName(eventId) + "` >= " + minAgeInclusive + "\n" +
                "          AND c.`" + ageColumnName(eventId) + "` <= " + maxAgeInclusive + "\n" +
                "          AND c.`gender` = " + gender + ";";
    }
    public static String createScoreViewForAllCompetitors(String viewName, long eventId) {
        return "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`date_of_birth`\n" +
                "    from `CompetitionManager`.`score` s\n" +
                "    left join `CompetitionManager`.`competitor` c\n" +
                "        on s.`competitor_id` = c.`id`\n" +
                "    where     s.`deleted` = false\n" +
                "          AND s.`is_valid` = true\n" +
                "          AND s.`event_id` = "+ eventId + ";";
    }
    public static String createCategory(long eventId, String name, String description, int minAgeInclusive, int maxAgeInclusive, Gender gender){
        return "INSERT INTO CompetitionManager.category" +
                "(event_id, name, description, min_age_inclusive, max_age_inclusive, gender)" +
                " values (" + eventId + ", '" + name + "', '" + description + "', " + minAgeInclusive + ", " + maxAgeInclusive + ", " + gender.getValue() + ");";
    }
    public static String createCompetitor(String name,Gender gender, LocalDate date_of_birth ){

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String created_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);

        return "INSERT INTO CompetitionManager.competitor" +
                "(name, gender, date_of_birth, created_datetime, deleted)" +
                " values ('" + name + "', " + gender.getValue() + ", '" + date_of_birth + "', '" + created_datetime + "' , 0);";
    }
}
