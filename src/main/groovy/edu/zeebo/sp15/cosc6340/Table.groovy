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

	void addRow(List values) {
		Row row = new Row()
		description.keySet().eachWithIndex { String field, int i ->
			if (values.size() > i) {
				println values[i]
				switch (description[field]) {
					case 'S': row[field] = values[i] as String; break
					case 'I':
						try {
							row[field] = values[i] as Integer
						}
						catch(NumberFormatException nfe) {
							throw new IllegalArgumentException("Invalid field value: Field $field expects an int")
						}
						break
				}
			}
		}

		println filename
		println row.contents

		new File(filename).withWriterAppend {
			it.println description.keySet().findAll { String field -> row[field] }.collect { String field ->
				[field, row[field]].join('\0')
			}.join('\0\0')
		}
		size++
	}

	private int size

	int getSize() {
		if (size == 0) {
			File file = new File(filename)
			if (file.exists()) {
				size = file.readLines().size()
			}
		}
		return size
	}

	// because you can't do tableObj.new Row() in Groovy :(
	Row newRow() { new Row() }

	class Row {
		private LinkedHashMap contents = [:]

		Row parse(String rowString) {
			println rowString
			rowString.split('\0\0').each {
				it.split('\0').with {
					contents[it[0]] = parseField(it[0], it[1])
				}
			}
			return this
		}

		def parseField(String field, def value) {
			if (value == 'null') {
				return null
			}

			switch (description[field]) {
				case 'S': return value as String
				case 'I':
					try {
						return value as Integer
					}
					catch(NumberFormatException nfe) {
						throw new IllegalArgumentException("Invalid field value: Field $field expects an int")
					}
			}
		}

		def getAt(String field) {
			contents[field]
		}

		def putAt(String field, def value) {
			contents[field] = parseField(field, value)
		}
	}

	class TableIterator implements Iterator<Row> {

		Scanner scan

		public TableIterator() {
			new File(filename).with {
				if (it.exists()) {
					scan = new Scanner(it)
				}
			}
		}

		@Override
		boolean hasNext() {
			return scan != null && scan.hasNextLine()
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
