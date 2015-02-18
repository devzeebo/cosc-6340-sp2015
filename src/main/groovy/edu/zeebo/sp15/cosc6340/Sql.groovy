package edu.zeebo.sp15.cosc6340

import edu.zeebo.sp15.cosc6340.Table

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

	static void readDataDictionary(String filename) {
		File dbFile = new File(filename)
		if (dbFile.exists()) {
			dbFile.withReader {
				Scanner scan = new Scanner(it)
				while (scan.hasNextLine()) {
					def line = scan.nextLine().split('\0\0')
					tables[line[0]].description[line[1]] = line[2]
				}
			}
		}
	}

	static void writeDataDictionary(String filename) {
		new File(filename).withWriter { writer ->
			tables.each { name, value ->
				value.description.each { field, type ->
					writer.println "$name\0\0$field\0\0$type\0\0$value.filename"
				}
			}
		}
	}

	static Select select(String... fields) {
		return new Select(query: new Query(fields: fields))
	}

	static void createTable(String name, Map<String, String> fields) {
		tables[name].description = fields
	}

	static void insertInto(String name, Object... values) {
		tables[name].addRow values
	}

	static class Query {

		private Table table

		private def fields

		private def where = [:]

		List<Map> execute() {

			table.findAll { row ->
				where.every { field, value ->
					row[field] == value
				}
			}.collect { row ->
				fields.collectEntries {
					[(it): row[it]]
				}
			}
		}
	}

	static class Select {

		private Query query

		From from(String tableName) {
			query.table = tables[tableName]
			if (query.fields == ['*']) {
				query.fields = query.table.description.keySet()
			}
			return new From(query: query)
		}
	}

	static class From {

		private Query query

		Query where(String field, String value) {
			query.where << [(field): value]
			return query
		}

		List<Map> execute() { query.execute() }
	}
}
