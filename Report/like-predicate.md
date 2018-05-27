### We had used "LIKE" in DbServlet:
    • matching title and director names
    • matching the star names

For examples, the in order to match the string in the pattern of "David" with
an input "D", we had use the like clause and wildcard symbol "%" to match the
subsequent characters (E.g. "D%"). This will only perform matching comparison
in the way that the characters after the input characters. We had also thought
about the case sensitive issues in the comparisons, so we converted all cases
both in the query and the input to lower case and did comparison from there.
Thus, even the matching characters are not the same letter case, the result
will still consider itself a valid output because of the conversion.


The example java code we had used:

```java
queryCount = String.format("select count(*) as total from movies " +
						"where lower(title) like lower(%s) AND " +
						"lower(director) like lower(%s)" +
						(yearQuery.equals("") ? ";" : (" AND year="
                        + yearQuery + ";")), ("'" + titleQuery+"%'"), ("'"
                         + directorQuery +"%'"));
System.out.println(queryCount);
```

**_Link to this file_**: [DbServlet.java on Github](https://github.com/UCI-Chenli-teaching/cs122b-spring18-team-3/blob/dev/project/src/DbServlet.java)
