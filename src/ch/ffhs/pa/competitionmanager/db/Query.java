package ch.ffhs.pa.competitionmanager.db;

import ch.ffhs.pa.competitionmanager.enums.Gender;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Contains strings for querying the database.
 */
public class Query {

    /**
     * Give back a SQL statement to get the row from 'event' table that has specific id.
     * @return A SQL statement to get the row from 'event' table that has specific id.
     */
    public static String eventById(long id) {
        return "select *\n" +
                "from `CompetitionManager`.`event`\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Get all events from the database.
     * @return A
     */
    public static String getAllEvents() {
        return "select *\n" +
                "from `CompetitionManager`.`event`\n" +
                "where `deleted` = false\n" +
                "order by `date` desc;";
    }

    /**
     * Give back a SQL statement that will drop the column called 'age_<event_id>' in table 'competitor'.
     * @param event_id The id of the event.
     * @return A SQL statement that will drop the column called 'age_<event_id>' in table 'competitor'.
     */
    public static String dropAgeColumn(long event_id) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "DROP COLUMN `" + ageColumnName(event_id) + "`;";
    }

    /**
     * Give back a SQL statement that wil create an age column in table 'competitor' which contains the age in years.
     * @param event_id The id of the event.
     * @param dateOfToday A date string ISO-8601 format uuuu-MM-dd, like '2019-01-01'.
     * @return A SQL statement that wil create an age column in table 'competitor' which contains the age in years.
     */
    static String createAgeColumn(long event_id, String dateOfToday) {
        return "ALTER TABLE `CompetitionManager`.`competitor`\n" +
                "ADD COLUMN `" + ageColumnName(event_id) + "` INT\n" +
                "AS (\n" +
                "    YEAR('" + dateOfToday + "') -\n" +
                "    YEAR(`date_of_birth`) - \n" +
                "    IF(STR_TO_DATE(CONCAT(YEAR('" + dateOfToday + "'), '-', MONTH(`date_of_birth`), '-', DAY(`date_of_birth`)) ,'%Y-%c-%e') > '" + dateOfToday + "', 1, 0)\n" +
                ");";
    }

    /**
     * Given an event id, returns a SQL statement that will count the number of valid scores in table 'score'.
     * @param event The id of the event.
     * @return A SQL statement that will count the number of valid scores in table 'score'.
     */
    public static String numberOfValidScores(long event) {
        return "select sum(`is_valid`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `event_id` = " + event + ";";
    }

    /**
     * Given an event id, returns a SQL statement that will return the maximum id in table 'score'.
     * @param event The id of the event
     * @return A SQL statement that will return the maximum id in table 'score'
     */
    public static String maxIdInScores(long event) {
        return "select max(`id`)\n" +
                "from `CompetitionManager`.`score`\n" +
                "where `is_valid` = true\n" +
                "  and `event_id` = " + event + ";";
    }

    /**
     * Get the score for a specific category
     * @param event_id The id of the event
     * @param viewName The name of the database view
     * @param isTimeRelevant Indicating if time is relevant, rather than points.
     * @return An SQL statement that will get all scores for the category.
     */
    public static String getScoresForCategory(long event_id, String viewName, boolean isTimeRelevant) {
        String queryPart1 =
                "SELECT DISTINCT s1.*\n" +
                "FROM `CompetitionManager`.`" + viewName + "` s1\n" +
                "LEFT JOIN `CompetitionManager`.`" + viewName + "` s2\n";

        String queryPart2;
        if (isTimeRelevant) {
            queryPart2 = "ON s1.`competitor_id` = s2.`competitor_id` AND timediff(s1.`time_needed`, s2.`time_needed`) > 0\n" +
                    "WHERE s2.`time_needed` IS NULL\n" +
                    "GROUP BY s1.`competitor_id`\n" +
                    "ORDER BY s1.`time_needed` ASC;";
        } else {
            queryPart2 = "ON s1.`competitor_id` = s2.`competitor_id` AND s1.`points_achieved` > s2.`points_achieved`\n" +
                    "WHERE s2.`points_achieved` IS NULL\n" +
                    "GROUP BY s1.`competitor_id`\n" +
                    "ORDER BY s1.`points_achieved` DESC;";
        }
        return queryPart1 + queryPart2;
    }

    /**
     * Get all scores
     * @param event_id The id of the event
     * @param withDeletedOnes Indicating if deleted scores should be loaded as well.
     * @param orderByName Indicating if the scores should be ordered by name (true) rather than by time_needed or points_achieved (false).
     * @return An SQL statement that will get all scores
     */
    public static String getAllScores(long event_id, boolean withDeletedOnes, boolean orderByName) {
        String sql =
                "select s.*, c.`name`, c.`date_of_birth`, c.`gender`, c.`" + ageColumnName(event_id) + "`\n" +
                " from `CompetitionManager`.`score` s\n" +
                " left join `CompetitionManager`.`competitor` c\n" +
                "    on s.`competitor_id` = c.`id`\n" +
                " where s.`event_id` = " + event_id + "\n" +
                "   and c.`deleted` = false";
        if (!withDeletedOnes) {
            sql += " and s.`deleted` = false";
        }
        if (orderByName) {
            sql += " order by c.`name` asc;";
        } else {
            sql += " order by s.`time_needed` asc;";
        }
        return sql;
    }

    /**
     * Give the age column name for specifiy event
     * @param event_id The id of the event
     * @return The according age column name (not an SQL statement)
     */
    public static String ageColumnName(long event_id) {
        return "age_" + event_id;
    }

    /**
     * Get all competitors
     * @return an SQL statement that will get all competitors
     */
    public static String getAllCompetitors() {
        return "select *\n" +
                "from `CompetitionManager`.`competitor`\n" +
                "where `deleted` = false\n" +
                "order by `name` asc;";
    }

    /**
     * Get all competitors having scores for a specific event.
     * @param event_id The id of the event.
     * @return An SQL statement that will get all competitors having scores for a specific event.
     */
    public static String getCompetitorsWithScoreForEvent(long event_id) {
        return "select distinct c.*\n" +
                "from `CompetitionManager`.`competitor` c\n" +
                "inner join `CompetitionManager`.`score` s\n" +
                "    on c.`id` = s.`competitor_id`\n" +
                "where     s.`event_id` = " + event_id + "\n" +
                "      and s.`deleted` = false;";
    }

    // --------------------------------------------
    // Methods to create SQL statements for categories
    // --------------------------------------------

    /**
     * Get all categories for specific event.
     * @param event_id The id of the event.
     * @return An SQL statement that will get all categories for specific event.
     */
    public static String getAllCategories(long event_id) {
        return "select *\n" +
                "from `CompetitionManager`.`category`\n" +
                "where `event_id` = " + event_id + "\n" +
                "  and `deleted` = false;";
    }

    /**
     * Get all view names
     * @return An SQL statement that will get all view names
     */
    public static String getAllViewNames() {
        return "SHOW FULL TABLES IN `CompetitionManager` WHERE TABLE_TYPE LIKE '%VIEW%';";
    }

    /**
     * Concatenate the score category view name for a specific event. This is not category specific.
     * @param event_id The event id.
     * @return The according view name (not an SQL statement)
     */
    public static String categoryViewEventName(long event_id) {
        return "scores_category_" + event_id;
    }

    /**
     * Concatenate the score category view name for a specific event and a specific category.
     * @param event_id The id of the event
     * @param category_id The id of the category
     * @return The according view name (not an SQL statement)
     */
    public static String categoryViewFullName(long event_id, long category_id) {
        return "scores_category_" + event_id + "_" + category_id;
    }

    /**
     * Drop a view.
     * @param viewName The name of the view
     * @return An SQL statement to drop that view
     */
    public static String dropView(String viewName) {
        return "drop view `CompetitionManager`.`" + viewName + "`;";
    }

    /**
     * Create a score view for a category.
     * @param event_id The id of the event
     * @param viewName The name of the view
     * @param minAgeInclusive The minimum age of the category (inclusive)
     * @param maxAgeInclusive The maximum age of the category (exclusive)
     * @param gender The gender of the category
     * @return An SQL statement that will create the score view
     */
    public static String createScoreViewForCategory(long event_id, String viewName, int minAgeInclusive, int maxAgeInclusive, int gender) {
        String sql =
                "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`gender`, c.`date_of_birth`, c.`" + ageColumnName(event_id) + "`\n" +
                "from `CompetitionManager`.`score` s\n" +
                "left join `CompetitionManager`.`competitor` c\n" +
                "on s.`competitor_id` = c.`id`\n" +
                "where" +
                "      s.`deleted` = false\n" +
                "  AND s.`is_valid` = true\n" +
                "  AND s.`event_id` = " + event_id + "\n" +
                "  AND c.`" + ageColumnName(event_id) + "` >= " + minAgeInclusive + "\n" +
                "  AND c.`" + ageColumnName(event_id) + "` <= " + maxAgeInclusive + "\n";
        if (Gender.valueOf(gender) == Gender.NOT_RELEVANT) {
            sql += ";";
        } else {
            sql += "  AND c.`gender` = " + gender + ";";
        }
        return sql;
    }

    /**
     * Create a score view that shows scores of all competitors, independently of categories.
     * @param viewName The name of the view
     * @param event_id The id of the event
     * @return
     */
    public static String createScoreViewForAllCompetitors(String viewName, long event_id) {
        return "create view `CompetitionManager`.`" + viewName + "` as\n" +
                "select s.*, c.`name`, c.`date_of_birth`\n" +
                "from `CompetitionManager`.`score` s\n" +
                "left join `CompetitionManager`.`competitor` c\n" +
                "    on s.`competitor_id` = c.`id`\n" +
                "where     s.`deleted` = false\n" +
                "      AND s.`is_valid` = true\n" +
                "      AND s.`event_id` = "+ event_id + ";";
    }
    /**
     * Creates a Category in the Database
     * @param event_id Event ID of the Event
     * @param name Event Name
     * @param description Event Description
     * @param minAgeInclusive Lower Age Level which includes Competitor to the Category
     * @param maxAgeInclusive Higher Age Level which includes Competitor to the Category
     * @param gender Genders which are for that Category
     * @return A SQL statement which creates a Event Entry in the Database
     */
    public static String createCategory(long event_id, String name, String description, int minAgeInclusive, int maxAgeInclusive, Gender gender){
        return "INSERT INTO CompetitionManager.category" +
                "(event_id, name, description, min_age_inclusive, max_age_inclusive, gender, deleted)" +
                " values (" + event_id + ", '" + name + "', '" + description + "', " + minAgeInclusive + ", " + maxAgeInclusive + ", " + gender.getValue() + ", false);";
    }

    /**
     * Creates a Competitor in the Database
     * @param name Name of the Competitor
     * @param gender Gender of the Competitor
     * @param date_of_birth Birthdate of Competitor
     * @return A SQL statement which creates a Competitor Entry in the Database
     */
    public static String createCompetitor(String name, Gender gender, LocalDate date_of_birth ){

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
     * @return A SQL statement which creates a Event in the Database
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
     * @param event_id Event ID which is relevant for this Score
     * @param competitor_id Competitor ID which is relevant for this Score
     * @param time_needed Time which was needed for this Score
     * @param points_achieved Points which are achived for this Score
     * @param number_of_tries How many tries are needed for this Score
     * @param is_valid If the Score is valid
     * @param time_of_recording When the Score has been recorded
     * @return A SQL statement which creates a Score Entry in Database
     */
    public static String createScore(long event_id, long competitor_id, LocalTime time_needed, double points_achieved, int number_of_tries, boolean is_valid, LocalDateTime time_of_recording){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String created_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);

        String timeNeededString;
        if (time_needed == null) {
            timeNeededString = "null";
        } else {
            timeNeededString = "'" + time_needed.toString() + "'";
        }
        return "INSERT INTO CompetitionManager.score" +
                "(event_id,competitor_id,time_needed,points_achieved, number_of_tries," +
                "is_valid, time_of_recording, created_datetime, deleted) VALUES" +
                "(" + event_id + ", " + competitor_id + ", " + timeNeededString + ", " + points_achieved  + ", " + number_of_tries +
                ", " + is_valid + ", '" + time_of_recording + "', '" + created_datetime + "',0);";
    }

    /**
     * Update a competitor.
     * @param id
     * @param name
     * @param gender
     * @param date_of_birth
     * @return An SQL statement that will perform the update.
     */
    public static String updateCompetitor(long id, String name, Gender gender, LocalDate date_of_birth) {
        return "update `CompetitionManager`.`competitor`\n" +
                "set `name` = '" + name + "', `gender` = " + gender.getValue() + ", `date_of_birth` = '" + date_of_birth + "'\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Update an event.
     * @param id
     * @param name
     * @param date
     * @param date_descr
     * @param description
     * @param is_time_relevant
     * @return An SQL statement that will perform the update.
     */
    public static String updateEvent(long id, String name,  LocalDate date, String date_descr, String description, boolean is_time_relevant) {
        return "update  `CompetitionManager`.`event`\n" +
                "set `name` = '" + name + "', `date` = '" + date + "', `date_descr` = '" + date_descr + "', `description` = '" + description + "'\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Update a score.
     * @param id
     * @param event_id
     * @param competitor_id
     * @param time_needed
     * @param points_achieved
     * @param number_of_tries
     * @param is_valid
     * @param time_of_recording
     * @param deleted
     * @return An SQL statement that will perform the update.
     */
    public static String updateScore(long id, long event_id, long competitor_id, LocalTime time_needed, double points_achieved, int number_of_tries, boolean is_valid, LocalDateTime time_of_recording, boolean deleted) {
        String timeNeededString;
        if (time_needed == null) {
            timeNeededString = "null";
        } else {
            timeNeededString = "'" + time_needed.toString() + "'";
        }
        return "update `CompetitionManager`.`score`\n" +
                "set `event_id` = " + event_id + ", `competitor_id` = " + competitor_id + ", `time_needed` = " + timeNeededString + ", `points_achieved` = " + points_achieved + ", `number_of_tries` = " + number_of_tries + ", `is_valid` = " + is_valid + ", `time_of_recording` = '" + time_of_recording + "', deleted = " + deleted + "\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Update a category.
     * @param id
     * @param event_id
     * @param name
     * @param description
     * @param minAgeInclusive
     * @param maxAgeInclusive
     * @param gender
     * @return An SQL statement that will perform the update.
     */
    public static String updateCategory(long id, long event_id, String name, String description, int minAgeInclusive, int maxAgeInclusive, Gender gender) {
        return "update `CompetitionManager`.`category`\n" +
                "set `event_id` = " + event_id + ", `name` = '" + name + "', `description` = '" + description + "', `min_age_inclusive` = " + minAgeInclusive + ", `max_age_inclusive` = " + maxAgeInclusive + ", `gender` = " + gender.getValue() + "\n" +
                "where `id` = " + id + ";";
    }

    /**
     * Sets the delete flag with the current timestamp in the database
     * @param id Competitor ID which has to be deleted
     * @return A SQL Statement which sets deleted to true
     */
    public static String deleteCompetitor(long id){
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
    public static String deleteEvent(long id){
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
    public static String deleteScore(long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE score SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }

    /**
     * Sets the delete flag with the current timestamp in the database
     * @param id Category ID which has to be deleted
     * @return A SQL Statement which sets deleted to true
     */
    public static String deleteCategory(long id){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String deleted_datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
        return "UPDATE category SET deleted = 1, deleted_datetime = '"
                + deleted_datetime + "' WHERE id = " + id + ";";
    }

    /**
     * Check if the database schema exists.
     * @return An SQL statement that will check if the database schema exists.
     */
    public static String doesDatabaseSchemaExist() {
        return  "SELECT SCHEMA_NAME\n" +
                "FROM INFORMATION_SCHEMA.SCHEMATA\n" +
                "WHERE SCHEMA_NAME = 'CompetitionManager'";
    }

    /**
     * Creates the database schema
     * @return An array holding SQL statements that have to be executed sequentially.
     */
    public static String[] createDatabaseSchema() {
        String sql = "SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;\n" +
                "SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;\n" +
                "SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Schema CompetitionManager\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE SCHEMA IF NOT EXISTS `CompetitionManager` DEFAULT CHARACTER SET utf8 ;\n" +
                "USE `CompetitionManager` ;\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `CompetitionManager`.`event`\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS `CompetitionManager`.`event` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(90) NOT NULL,\n" +
                "  `date` DATE NOT NULL,\n" +
                "  `date_descr` VARCHAR(90) NULL,\n" +
                "  `description` VARCHAR(1024) NULL,\n" +
                "  `is_time_relevant` TINYINT NOT NULL,\n" +
                "  `created_datetime` DATETIME NOT NULL,\n" +
                "  `deleted` BIT NOT NULL,\n" +
                "  `deleted_datetime` DATETIME NULL,\n" +
                "  PRIMARY KEY (`id`))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `CompetitionManager`.`competitor`\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS `CompetitionManager`.`competitor` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(90) NOT NULL,\n" +
                "  `gender` TINYINT NOT NULL,\n" +
                "  `date_of_birth` DATE NOT NULL,\n" +
                "  `created_datetime` DATETIME NOT NULL,\n" +
                "  `deleted` BIT NOT NULL,\n" +
                "  `deleted_datetime` DATETIME NULL,\n" +
                "  PRIMARY KEY (`id`))\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `CompetitionManager`.`score`\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS `CompetitionManager`.`score` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `event_id` INT NOT NULL,\n" +
                "  `competitor_id` INT NOT NULL,\n" +
                "  `time_needed` TIME(3) NULL,\n" +
                "  `points_achieved` DOUBLE NULL,\n" +
                "  `number_of_tries` INT NOT NULL,\n" +
                "  `is_valid` BIT NOT NULL,\n" +
                "  `time_of_recording` DATETIME NOT NULL,\n" +
                "  `created_datetime` DATETIME NOT NULL,\n" +
                "  `deleted` BIT NOT NULL,\n" +
                "  `deleted_datetime` DATETIME NULL,\n" +
                "  PRIMARY KEY (`id`, `event_id`, `competitor_id`),\n" +
                "  INDEX `fk_score_event1_idx` (`event_id` ASC) VISIBLE,\n" +
                "  INDEX `fk_score_competitor1_idx` (`competitor_id` ASC) VISIBLE,\n" +
                "  CONSTRAINT `fk_score_event1`\n" +
                "    FOREIGN KEY (`event_id`)\n" +
                "    REFERENCES `CompetitionManager`.`event` (`id`)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION,\n" +
                "  CONSTRAINT `fk_score_competitor1`\n" +
                "    FOREIGN KEY (`competitor_id`)\n" +
                "    REFERENCES `CompetitionManager`.`competitor` (`id`)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "-- -----------------------------------------------------\n" +
                "-- Table `CompetitionManager`.`category`\n" +
                "-- -----------------------------------------------------\n" +
                "CREATE TABLE IF NOT EXISTS `CompetitionManager`.`category` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `event_id` INT NOT NULL,\n" +
                "  `name` VARCHAR(90) NOT NULL,\n" +
                "  `description` VARCHAR(1024) NULL,\n" +
                "  `min_age_inclusive` INT NOT NULL,\n" +
                "  `max_age_inclusive` INT NOT NULL,\n" +
                "  `gender` TINYINT NOT NULL,\n" +
                "  `deleted` BIT NOT NULL,\n" +
                "  `deleted_datetime` DATETIME NULL,\n" +
                "  PRIMARY KEY (`id`, `event_id`),\n" +
                "  INDEX `fk_category_event1_idx` (`event_id` ASC) VISIBLE,\n" +
                "  CONSTRAINT `fk_category_event1`\n" +
                "    FOREIGN KEY (`event_id`)\n" +
                "    REFERENCES `CompetitionManager`.`event` (`id`)\n" +
                "    ON DELETE NO ACTION\n" +
                "    ON UPDATE NO ACTION)\n" +
                "ENGINE = InnoDB;\n" +
                "\n" +
                "\n" +
                "SET SQL_MODE=@OLD_SQL_MODE;\n" +
                "SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;\n" +
                "SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;\n";

        return splitAndAdd(sql, ";");
    }

    public static String[] createTestData() {
        String sql =
                "-- Das ist wichtig, damit geprueft wird, ob foreign keys ueberhaupt vorhanden sind!!!\n" +
                "SET FOREIGN_KEY_CHECKS=1;\n" +
                "-- This way you can check the status.\n" +
                "SELECT @@FOREIGN_KEY_CHECKS;\n" +
                "\n" +
                "\n" +
                "-- -------------------- event --------------------\n" +
                "insert into `CompetitionManager`.`event`\n" +
                "    (`name`, `date`, `date_descr`, `description`, `is_time_relevant`, `created_datetime`, `deleted`, `deleted_datetime`) values \n" +
                "\t(\"Ninja Warriors 2018\", \"2018-09-11\", \"11. September 2018\", \"First event organized with software\", true, \"2018-08-10 08:54:12\", false, null),\t# id=1\n" +
                "\t(\"Ninja Warriors 2019\", \"2019-09-13\", \"13. September 2019\", null,                                  true, \"2018-08-27 18:43:19\", false, null);\t# id=2\n" +
                "-- select * from `CompetitionManager`.`event`;\n" +
                "\n" +
                "\n" +
                "-- -------------------- competitor --------------------\n" +
                "insert into `CompetitionManager`.`competitor`\n" +
                "    (`name`, `gender`, `date_of_birth`, `created_datetime`, `deleted`, `deleted_datetime`) values \n" +
                "\t-- Event 2018\n" +
                "    -- Erwachsene, Männer\n" +
                "    (\"Hans Ulrich Müller\", 1, \"1958-01-12\", \"2018-09-11 10:05:12\", false, null),\t# id=1\n" +
                "    (\"Herbert Winkler\",    1, \"1965-03-11\", \"2018-09-11 10:10:35\", false, null),\t# id=2\n" +
                "    (\"Ueli Schläpfi\",      1, \"1969-05-03\", \"2018-09-11 10:15:15\", false, null),\t# id=3\n" +
                "    -- Kinder, Jungs\n" +
                "    (\"Max Müller\",         1, \"2010-10-10\", \"2018-09-11 10:20:43\", false, null),\t# id=4\n" +
                "    (\"Andreas Juppi\",      1, \"2011-01-18\", \"2018-09-11 10:24:47\", false, null),\t# id=5\n" +
                "    (\"Georg Tanner\",       1, \"2011-02-24\", \"2018-09-11 10:28:31\", false, null),\t# id=6\n" +
                "    -- Erwachsene, Frauen\n" +
                "    (\"Nina Goetschi\",      2, \"1965-08-23\", \"2018-09-11 10:15:24\", false, null),\t# id=7\n" +
                "    (\"Lisa Grüniger\",      2, \"1978-11-01\", \"2018-09-11 10:19:26\", false, null),\t# id=8\n" +
                "    (\"Marta Rucker\",       2, \"1989-08-02\", \"2018-09-11 10:23:54\", false, null),\t# id=9\n" +
                "    \n" +
                "    -- Event 2019\n" +
                "    -- Erwachsene, Männer\n" +
                "    (\"Matrin Marxer\",      1, \"1986-02-05\", \"2018-09-13 10:01:19\", false, null),\t# id=10\n" +
                "\t(\"Klaus Ammann\",       1, \"1988-04-09\", \"2018-09-13 10:04:49\", false, null),\t# id=11\n" +
                "    (\"Paul Heimgrätner\",   1, \"1971-07-22\", \"2018-09-13 10:11:28\", false, null);\t# id=12\n" +
                "-- select * from `CompetitionManager`.`competitor`;\n" +
                "\n" +
                "\n" +
                "-- -------------------- score --------------------\n" +
                "insert into `CompetitionManager`.`score`\n" +
                "    (`event_id`, `competitor_id`, `time_needed`, `points_achieved`, `number_of_tries`, `is_valid`, `time_of_recording`, `created_datetime`, `deleted`, `deleted_datetime`) values\n" +
                "    -- Event 2018\n" +
                "    -- Erwachsene, Männer\n" +
                "    -- Hans Ulrich Müller\n" +
                "    (1, 1, \"00:05:12\", null, 1, true,  \"2018-09-11 10:05:00\", \"2018-09-11 10:10:12\", false, null),\n" +
                "    (1, 1,       null, null, 2, false, \"2018-09-11 10:10:30\", \"2018-09-11 10:15:45\", false, null),\n" +
                "    (1, 1, \"00:03:06\", null, 3, true,  \"2018-09-11 10:16:00\", \"2018-09-11 10:08:10\", false, null),\n" +
                "    -- Herbert Winkler\n" +
                "    (1, 2, \"00:04:23\", null, 1, true,  \"2018-09-11 10:04:01\", \"2018-09-11 10:08:56\", false, null),\n" +
                "    -- Ueli Schläpfi\n" +
                "    (1, 3,       null, null, 1, false, \"2018-09-11 10:45:54\", \"2018-09-11 10:51:32\", false, null),\n" +
                "    (1, 3,       null, null, 1, false, \"2018-09-11 10:45:54\", \"2018-09-11 10:51:32\", false, null), -- Double scores should not be shon in the ranking.\n" +
                "    \n" +
                "    -- Kinder, Jungs\n" +
                "    -- Max Müller\n" +
                "    (1, 4, \"00:05:31\", null, 1, true,  \"2018-09-11 10:05:00\", \"2018-09-11 10:10:12\", false, null),\n" +
                "    (1, 4,       null, null, 2, false, \"2018-09-11 10:10:30\", \"2018-09-11 10:15:45\", false, null),\n" +
                "    (1, 4, \"00:04:12\", null, 3, true,  \"2018-09-11 10:16:00\", \"2018-09-11 10:08:10\", false, null),\n" +
                "    -- Andreas Juppi\n" +
                "    (1, 5, \"00:03:45.1\", null, 1, true,  \"2018-09-11 10:04:01\", \"2018-09-11 10:08:56\", false, null),\n" +
                "    -- Georg Tanner\n" +
                "    (1, 6, \"00:06:25\", null, 1, true,  \"2018-09-11 10:04:01\", \"2018-09-11 10:08:56\", false, null),\n" +
                "    (1, 6, \"00:05:31\", null, 2, true,  \"2018-09-11 10:09:01\", \"2018-09-11 10:12:59\", false, null),\n" +
                "    \n" +
                "    -- Erwachsene, Frauen\n" +
                "    -- Nina Goetschi\n" +
                "    (1, 7, \"00:03:45.05\", null, 1, true,  \"2018-09-11 10:04:01\", \"2018-09-11 10:08:56\", false, null),\n" +
                "    -- Lisa Grüniger\n" +
                "    (1, 8, \"00:03:45.9\", null, 1, true,  \"2018-09-11 10:09:01\", \"2018-09-11 10:12:49\", false, null),\n" +
                "    -- Marta Rucker\n" +
                "    (1, 9,       null, null, 2, false, \"2018-09-11 10:10:30\", \"2018-09-11 10:15:45\", false, null),\n" +
                "    (1, 9,       null, null, 2, false, \"2018-09-11 10:15:31\", \"2018-09-11 10:21:38\", false, null),\n" +
                "    \n" +
                "    -- Event 2019\n" +
                "    -- Matrin Marxer\n" +
                "    (2, 10, \"00:06:25\", null, 2, false, \"2019-09-11 10:10:30\", \"2019-09-11 10:15:45\", false, null);\n" +
                "    \n" +
                "-- select * from `CompetitionManager`.`score`;\n" +
                "\n" +
                "\n" +
                "-- -------------------- category --------------------\n" +
                "insert into `CompetitionManager`.`category`\n" +
                "    (`event_id`, `name`, `description`, `min_age_inclusive`, `max_age_inclusive`, `gender`,`deleted`,`deleted_datetime`) values\n" +
                "    (1, \"Jungs\",    \"Kinder, männlich, zwischen 0 bis und mit 17 Jahre alt\", 0,  17, 1, 0,NULL),\n" +
                "    (1, \"Mädchen\",  \"Kinder, weiblich, zwischen 0 bis und mit 17 Jahre alt\", 0,  17, 1, 0,NULL),\n" +
                "    (1, \"Männer\",   \"Erwachsene, männlich, ab 18 Jahren\",                   18, 999, 1, 0,NULL),\n" +
                "    (1, \"Frauen\",   \"Erwachsene, weiblich, ab 18 Jahren\",                   18, 999, 2, 0,NULL),\n" +
                "    (1, \"Alle\",     \"Alle Teilnehmer, unabhängig von Alter und Geschlecht\",  0, 999, 3, 0,NULL);\n" +
                "    \n" +
                "-- select * from `CompetitionManager`.`category`;";

        return splitAndAdd(sql, ";");
    }

    public static String dropSchema() {
        return "drop schema `CompetitionManager`;";
    }

    private static String[] splitAndAdd(String s, String pattern) {
        String[] splitted = s.split(pattern);

        //Pattern p = Pattern.compile("( |\n)*");
        for (int i = 0; i<splitted.length; i++) {
            //if (!p.matcher(sqlSplitted[i]).matches()) {
            splitted[i] = splitted[i] + pattern;
            //}
        }
        //splitted = Arrays.stream(splitted).filter(s -> s.matches(" *[a-zA-Z0-9].*;")).toArray(String[]::new);

        return splitted;
    }
}
