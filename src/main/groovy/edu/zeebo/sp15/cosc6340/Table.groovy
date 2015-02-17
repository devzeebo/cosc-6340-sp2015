package edu.zeebo.sp15.cosc6340

import java.util.function.Consumer

/**
 * User: Eric
 * Date: 2/17/15
 */
class Table {

	String name

	String filename

	LinkedHashMap description = [:]

	Iterator<Row> iterator() { new TableIterator() }

	void addRow(def values) {
		Row row = new Row()
		description.keySet().eachWithIndex { String field, int i ->
			if (values.size() > i) {
				switch (description[field]) {
					case 'S': row[field] = values[i] as String; break
					case 'I': row[field] = values[i] as Integer; break
				}
			}
		}

		new File(filename).withWriterAppend {
			it.println description.keySet().findAll { String field -> row[field] }.collect { String field ->
				[field, row[field]].join('\0')
			}.join('\0\0')
		}
	}

	class Row {
		private LinkedHashMap contents = [:]

		Row parse(String rowString) {
			rowString.split('\0\0').each {
				it.split('\0').with {
					contents[it[0]] = it[1]
				}
			}
			return this
		}

		def getAt(String fieldName) {
			switch (description[fieldName]) {
				case 'S': return getString(fieldName)
				case 'I': return getInt(fieldName)
			}
		}

		def putAt(String field, def value) {
			contents[field] = value
		}

		Integer getInt(String fieldName) { contents[fieldName].with { it == 'null' ? null : it as Integer } }

		String getString(String fieldName) { contents[fieldName] as String }
	}

	class TableIterator implements Iterator<Row> {

		Scanner scan = new Scanner(new File(filename))

		@Override
		boolean hasNext() {
			return scan.hasNextLine()
		}

		@Override
		Row next() {
			return new Row().parse(scan.nextLine())
		}

		@Override
		void remove() {
			throw new UnsupportedOperationException('Use a delete SQL command')
		}

		@Override
		void forEachRemaining(Consumer<? super Row> action) {
			throw new UnsupportedOperationException('Not here...')
		}
	}
}
