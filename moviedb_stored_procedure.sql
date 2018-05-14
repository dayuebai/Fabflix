DELIMITER //
CREATE PROCEDURE add_single_star(IN starName VARCHAR(100), IN birthYear INT(11))
 BEGIN
 DECLARE i VARCHAR(10) DEFAULT '';
 DECLARE j INT(10) DEFAULT 0;

 SELECT max(id) INTO i FROM stars;
 SELECT TRIM(LEADING 'nm' FROM i) INTO i;
 SELECT i+1 INTO j;
 SELECT CONCAT('nm',j) INTO i;
 INSERT INTO stars(id,name,birthYear)
 VALUES(i,starName,birthYear);
 END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE add_movie(IN title VARCHAR(100),IN year INT(11),IN director VARCHAR(100),IN star VARCHAR(100),IN birthYear INT(11),IN genre VARCHAR(32),OUT total INT(2))
 BEGIN
 DECLARE i INT(2) DEFAULT 0;
 DECLARE j VARCHAR(10) DEFAULT '';
 DECLARE k INT(10) DEFAULT 0;
 DECLARE l INT(10) DEFAULT 0;
 DECLARE m INT(10) DEFAULT 0;
 DECLARE n VARCHAR(10) DEFAULT '';
 DECLARE o INT(11) DEFAULT 0;
 SELECT COUNT(*) INTO i from movies where movies.title=title AND movies.year=year AND movies.director=director;
 IF i > 0 THEN
 	SET total=0;
 ELSE 
 	SET total=1;
 	SELECT max(id) INTO j FROM movies;
 	SELECT TRIM(LEADING 'tt0' FROM j) INTO j;
 	SELECT j+1 INTO k;
 	SELECT CONCAT('tt0', k) INTO j;
 	INSERT INTO movies(movies.id,movies.title,movies.year,movies.director)
 	VALUES(j,title,year,director);

 	SELECT COUNT(*) INTO l from stars where stars.name=star;
	 IF l=0 THEN
	 	CALL add_single_star(star,birthYear);
	 END IF;
	 SELECT COUNT(*) INTO m from genres where genres.name=genre;
	 IF m=0 THEN
	 	INSERT INTO genres(name) VALUES(genre);
	 END IF;
	 SELECT id INTO n FROM stars where stars.name=star;
	 SELECT id INTO o FROM genres where genres.name=genre;
	 INSERT INTO stars_in_movies(starId,movieId) 
	 VALUES(n,j);
	 INSERT INTO genres_in_movies(genreId,movieId)
	 VALUES(o,j);
 END IF;
 SELECT total;
 END //
DELIMITER ;
