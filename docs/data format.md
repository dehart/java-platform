# JSON format specification

The document specifies the format of the JSON data the server responds

## Form data

The `data` part of the response holds the actual model data that should be loaded into the form
It can also contain the relations of the model where has-many relations are shown as an array of data and many-many relation as an array of foreign keys
has-one relations show as an object.

The `message` part holds messages that should be shown to the user. Eg "Model was saved successful", "The form has errors" or "This model can no longer be changed"
Message objects contain multiple properties:
- type: ['error', 'warning', 'success', 'notice'] - This will show the user what kind of message this was.
- text: string - The text of the message that is shown to the user.

If the response contains `errors` this is formatted the very same as the `data` part of the response but only the data that contains errors is available and the value of the property is the error text

Example
~~~
{
	'data': {
		'contact' : {
			'name': 'Michael de Hart',
			'comment' : 'Wakes up at 10am',
			'created_at': '2014-10-21 21:27:22',
			'email_addresses': [
				{'id':1, 'type':'home', 'value': 'derinus@gmail.com'},
				{'id':2, 'type':'work', 'value': 'info@cloudengineering.nl'},
				{'id':3, 'type':'word', 'value': 'info@deskware.net'}
			],
			'phone_numbers': [
				{'id':4, 'value': '0612345678'}, 
				{'id':5, 'value': '0735512345'}
			],
			'addresses': [
				{'street': 'mainstreet 5', 'city': 'Eindhoven', 'country':'NL','postal': '5272GT'}
			],
			labels: [1,4]  // for many-many key only
		}
	},
	'messages':[
		{'code':100, 'type': 'error', 'text': 'The form contains errors'}
	],
	'errors': [
		'contact': {'name':'This field is required'}
	]
}
~~~

## Stores

When fetching multiple database records to be displayed into a list the data is formatted differently from the Form data
The records will not be in key:value format. When you are loading 100 record with 5 properties you do not want to repeat the attribute names 500 times.
Therefor the `records` part represents the data only and the `fields` part will specify there meaning only once.

Additional `data` can also be added to the store. The client can implement this the way they like.
This data is in key:value format since it represents 1 item.

The `messages` property is also available for stores

~~~
{
	'fields' : {'id', 'name', 'phone_nb', 'email', 'labels':[], 'address':{'id', 'place', 'street'}},
	'records' : [
		[1, 'Michael de Hart', '0612345678', 'info@cloudengineering.nl',[1,4], {2, 'Eindhoven', 'Mainstreet 1'}],
		[2, 'Yosemite Sam', '5551234567', 'yosemite@sam.org', [2], {4, 'New York', 'Secondstreet 2'}],
		[3, ...]
	],
	'data': {
		'total_items': 128,
		'sum_of_price': '1600.00'
	},
	'messages' : [
		{'type' : 'debug-sql', 'text': 'Query: SELECT * FROM contact LIMIT 100'}
	]
}
~~~
