package edu.zeebo.sp15.cosc6340

/**
 * User: Eric
 * Date: 2/17/15
 */
class Sql {

	private static Map<String, Table> tables = [:].withDefault { String key -> new Table(name: key, filename: "${key}.table") }

	static {
		readDataDictionary('hw1.db')

		Runtime.addShutdownHook {
			writeDataDictionary('hw1.db')
		}
	}

	/**
	 * Read the data dictionary file
	 *
	 * Data dictionary format is as follows:
	 * Each line represents a single field in a single table. Each attribute of the line is separated by two null
	 * characters (\0\0).
	 *
	 * Each line is in the following format:
	 * tableName\0\0fieldName\0\0fieldType\0\0tableFile(unused)
	 *
	 * @param filename the file containing the data dictionary
	 */
	static void readDataDictionary(String filename) {
		File dbFile = new File(filename)
		if (dbFile.exists()) {
			// Opens a reader over the file
			dbFile.withReader {
				// Scanner is easier to use than reader
				Scanner scan = new Scanner(it)
				while (scan.hasNextLine()) {
					// \0\0 is our split character
					def line = scan.nextLine().split('\0\0')
					tables[line[0]].description[line[1]] = line[2]
				}
			}
		}
	}

	/**
	 * Writes the data dictionary to a file
	 *
	 * @see Sql.readDataDictionary ( String ) for file format
	 *
	 * @param filename
	 */
	static void writeDataDictionary(String filename) {
		new File(filename).withWriter { writer ->
			// iterate over each table
			tables.each { name, value ->
				// iterate over each field in the table
				value.description.each { field, type ->
					writer.println "$name\0\0$field\0\0$type\0\0${value.filename}"
				}
			}
		}
	}

	static Select select(List<String> fields) {
		return new Select(query: new Query(fields: fields))
	}

	static void createTable(String name, Map<String, String> fields) {
		tables[name].description = fields.collectEntries { key, value -> [key, value[0]] }
	}

	static void insertInto(String name, List<String> values) {
		tables[name].addRow values
	}


	static String printTable(String tableName) {

		if (tables[tableName].size == 0) {
			return "$tableName\nEMPTY\n"
		}

		return printResults(tableName, select(['*']).from([tableName]).execute())
	}

	static String printResults(String title, List<Map> results) {
		println results
		def widths = results[0].keySet().collectEntries { field -> [field, field.length() + 2] }
		results.each { row ->
			row.keySet().each { field ->
				widths[field] = Math.max("${row[field]}".length() + 2, widths[field])
			}
		}
		println widths

		int width = widths.values().sum() + widths.size() - 1
		int padding = (widths.values().sum() - title.length()) / 2
		StringBuilder builder = new StringBuilder("|${widths.values().collect { '-' * it }.join('-')}|\n")
		builder.append "|${' ' * padding}$title${' ' * (width - padding - title.length())}|\n"
		builder.append "|${widths.values().collect { '-' * it }.join('+')}|\n"
		builder.append "|"
		builder.append widths.collect { field, w ->
			String.format(" %-${w - 2}s ", field)
		}.join('|')
		builder.append "|\n"
		builder.append "|${widths.values().collect { '-' * it }.join('+')}|\n"
		results.each { row ->
			builder.append '|'
			builder.append widths.collect { field, w ->
				String.format(" %-${w - 2}s ", row[field])
			}.join('|')
			builder.append "|\n"
		}
		builder.append "|${widths.values().collect { '-' * it }.join('+')}|\n"
		return builder.toString()
	}

	static class Query {

		private Table table

		protected def fields

		protected def where = [:]

		List<Map> execute() {

			println fields
			println where
			table.collect { row -> // projection transformation
				fields.findAll { row[it] }.collectEntries { // into a map of field: value pairs
					[(it): row[it]]
				}
			}
			// find all the rows
					.findAll { Map row ->
				// where every field in the row equals the value in the where clause
				where.every { field, value ->
					row[field] == value
				}
			}
		}

		static class ImplicitJoinQuery extends Query {

			private def joinOn

			List<Map> execute() {

				// select all the entries in the smallest table
				Table smallestTable = joinOn.keySet().min { it.size }
				Table otherTable = (joinOn.keySet() - smallestTable).first()

				println fields
				def implicitAddJoinFields = joinOn.values().collectEntries {
					[(it), !fields.contains(it)]
				}
				def allFields = [fields, joinOn.values()].flatten().unique()

				println implicitAddJoinFields
				if (allFields.contains("*")) {
					allFields = ["*"]
				}
				def tableResults = Sql.select(allFields) // project the fields and join fields
						.from([smallestTable.name]) // from the smallest table
						.execute()
						.collect { row -> // and join those
					Sql.select(allFields) // with the fields and join fields
							.from([otherTable.name]) // from the other table
							.where(joinOn[otherTable], row[joinOn[smallestTable]]) // where they join fields are equal
							.execute()
							.collect { it.putAll row; it } // and put the two maps together
				}.flatten() // and flatten the list of lists

				if (implicitAddJoinFields.values().contains(true)) {
					implicitAddJoinFields.each { field, remove ->
						if (!remove) {
							tableResults.each {
								it.remove(field)
							}
						}
					}
				}

				return tableResults
			}

			void setWhere(Map map) {
				// its only one entry
				map.each { key, value ->
					// do for both key and value (both field names)
					[key, value].each {
						joinOn[joinOn.keySet().find { Table table ->
							// find the table that has the field and doesn't have a joinOn assigned yet
							joinOn[table] == null && table.description.keySet().contains(it)
						}] = it // set the table to join on the field
					}
				}
			}
		}
	}

	static class Select {

		private Query query

		From from(List<String> tableNames) {
			if (tableNames.size() == 1) {
				query.table = tables[tableNames[0]]
				if (query.fields == ['*']) { // All fields requested
					query.fields = query.table.description.keySet()
				}
				return new From(query: query)
			} else {
				Query.ImplicitJoinQuery newQuery = new Query.ImplicitJoinQuery(fields: query.fields)
				newQuery.joinOn = tableNames.collectEntries { [(tables[it]): null] }
				return new From(query: newQuery)
			}
		}
	}

	static class From {

		private Query query

		Query where(String field, def value) {
			// set the where clause
			query.where = [(field): value]
			return query
		}

		List<Map> execute() { query.execute() }
	}

	public static void main(String[] args) {
		Sql.createTable('book', [title: 'S', author: 'S'])
//		Sql.createTable('author', [name: 'S', age: 'I'])
//
//		println Sql.select('title', 'age').from('book', 'author').where('author', 'name').execute()

		println Sql.printTable('book')
	}
}
