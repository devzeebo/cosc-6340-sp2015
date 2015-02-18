package edu.zeebo.sp15.cosc6340
/**
 * User: Eric
 */
class TableTest extends GroovyTestCase {

	def testTableFilename = 'table.test'
	def testTableFile = new File(testTableFilename)

	def testTableDescription = [
			string: 'S',
			int: 'I'
	]

	Table testTable

	void setUp() {
		testTable = new Table(name: 'test', filename: testTableFilename, description: testTableDescription)
	}

	void tearDown() {
		testTableFile.delete()
	}

	void testTableInsert() {
		def rows = [
				['hello world', 1],
				['justa string'],
				[1, 2], // this should work just fine
				['string again', '12345'] // so should this
		]

		rows.each {
			testTable.addRow(* it)
		}

		assert testTableFile.readLines().size() == 4
	}

	void testTableInvalidType() {
		def invalidRow = ['valid', 'invalid']

		def msg = shouldFail(IllegalArgumentException) {
			testTable.addRow(* invalidRow)
		}
		assert msg.contains('Field int')
	}

	void testTableIterator() {

		def rows = [
				['hello world', 1],
				['justa string'],
				[1, 2], // this should work just fine
				['string again', '12345'] // so should this
		]

		rows.each {
			testTable.addRow(* it)
		}

		testTable.eachWithIndex { Table.Row row, int i ->
			assert row.contents.string == rows[i][0] as String
			if (rows[i].size() > 1) {
				assert row.contents.int == rows[i][1] as int
			}
		}
	}

	// Row tests

	void testRowParse() {
		def rowDefinition = [['string', 'a string'].join('\0'), ['int', '12345'].join('\0')].join('\0\0')

		def row = testTable.newRow().parse(rowDefinition)

		assert row.contents.string == 'a string'
		assert row.contents.int == 12345
	}

	void testRowParseNull() {
		def rowDefinition = [['string', 'null'].join('\0'), ['int', 'null'].join('\0')].join('\0\0')

		def row = testTable.newRow().parse(rowDefinition)

		assert row.contents.string == null
		assert row.contents.int == null
	}

	void testRowParsePartial() {
		def strDef = ['string', 'a string'].join('\0')
		def intDef = ['int', '12345'].join('\0')

		def strRow = testTable.newRow().parse(strDef)
		assert strRow.contents.string == 'a string'
		assert !strRow.contents.containsKey('int')

		def intRow = testTable.newRow().parse(intDef)
		assert intRow.contents.int == 12345
		assert !intRow.contents.containsKey('string')
	}

	void testRowParseInvalid() {
		def rowDefinition = [['string', 'a string'].join('\0'), ['int', 'invalid type'].join('\0')].join('\0\0')

		def msg = shouldFail(IllegalArgumentException) {
			def row = testTable.newRow().parse(rowDefinition)
		}
		assert msg.contains('Field int')
	}

	void testRowParseField() {
		def row = testTable.newRow()

		def str = 'a string'
		def i = '12345'

		assert row.parseField('string', str) == str
		assert row.parseField('int', i) == i as Integer
	}

	void testRowParseFieldNull() {
		def row = testTable.newRow()

		def str = 'null'
		def i = 'null'

		assert row.parseField('string', str) == null
		assert row.parseField('int', i) == null
	}

	void testRowParseFieldInt() {
		def row = testTable.newRow()

		def i = 12345

		assert row.parseField('int', i) == i
	}
}
