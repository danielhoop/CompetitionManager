package ch.ffhs.pa.competitionmanager.db;

import ch.ffhs.pa.competitionmanager.enums.Gender;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    /**
     * Creates a Event in the Database
     * @param eventId Event ID of the Event
     * @param name Event Name
     * @param description Event Description
     * @param minAgeInclusive Lower Age Level which includes Competitor to the Category
     * @param maxAgeInclusive Higher Age Level which includes Competitor to the Category
     * @param gender Genders which are for that Category
     * @return A SQL String which creates a Event Entry in the Database
     */
    public static String createCategory(long eventId, String name, String description, int minAgeInclusive, int maxAgeInclusive, Gender gender){
        return "INSERT INTO CompetitionManager.category" +
                "(event_id, name, description, min_age_inclusive, max_age_inclusive, gender)" +
                " values (" + eventId + ", '" + name + "', '" + description + "', " + minAgeInclusive + ", " + maxAgeInclusive + ", " + gender.getValue() + ");";
    }

    /**
     * Creates a Competitor in the Database
     * @param name Name of the Competitor
     * @param gender Gender of the Competitor
     * @param date_of_birth Birthdate of Competitor
     * @return A SQL String which creates a Competitor Entry in the Database
     */
    public static String createCompetitor(String name,Gender gender, LocalDate date_of_birth ){

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String created_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);

        return "INSERT INTO CompetitionManager.competitor" +
                "(name, gender, date_of_birth, created_datetime, deleted)" +
                " values ('" + name + "', " + gender.getValue() + ", '" + date_of_birth + "', '" + created_datetime + "' , 0);";
    }

    /**
     * Creates a Event in the Database
     * @param name Name of the Event
     * @param date Date of the Event
     * @param date_descr Event Date Description
     * @param description Event Description
     * @param is_time_relevant Boolean if the Event is Time relevant
     * @return A SQL String which creates a Event in the Database
     */
    public static String createEvent(String name,  LocalDate date, String date_descr, String description, boolean is_time_relevant){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String created_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);

        return "INSERT INTO CompetitionManager.event" +
                "(name, date, date_descr, description, is_time_relevant, created_datetime, deleted)" +
                " values ('" + name + "', '" + date + "', '" + date_descr + "', '"
                + description + "' , " + is_time_relevant + ", '" + created_datetime + "',0);";
    }

    /**
     * Creates a Score in the Database
     * @param eventid Event ID which is relevant for this Score
     * @param competitor_id Competitor ID which is relevant for this Score
     * @param time_needed Time which was needed for this Score
     * @param points_achieved Points which are achived for this Score
     * @param number_of_tries How many tries are needed for this Score
     * @param is_valid If the Score is valid
     * @param time_of_recording When the Score has been recorded
     * @return A SQL String which creates a Score Entry in Database
     */
    public static String createScore(Long eventid, Long competitor_id, LocalTime time_needed, double points_achieved, int number_of_tries, boolean is_valid, LocalDateTime time_of_recording){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String created_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "INSERT INTO CompetitionManager.score" +
                "(event_id,competitor_id,time_needed,points_achieved, number_of_tries," +
                "is_valid, time_of_recording, created_datetime, deleted) VALUES" +
                "(" + eventid + ", " + competitor_id + ", '" + time_needed + "', " + points_achieved  + ", " + number_of_tries +
                ", " + is_valid + ", '" + time_of_recording + "', '" + created_datetime + "',0);";
    }

    /**
     * Sets the delete flag with the current timestamp in the database
     * @param id Competitor ID which has to be deleted
     * @return A SQL Statement which sets deleted to true
     */
    public static String deleteCompetitor(Long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE competitor SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }

    /**
     * Sets the delete flag with the current timestamp in the database
     * @param id Event ID which has to be deleted
     * @return A SQL Statement which sets deleted to true
     */
    public static String deleteEvent(Long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE event SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }

    /**
     * Sets the delete flag with the current timestamp in the database
     * @param id Score ID which has to be deleted
     * @return A SQL Statement which sets deleted to true
     */
    public static String deleteScore(Long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE score SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }

    public static String deleteCategory(Long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE category SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }
}
