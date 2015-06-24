# ChestShopLogger

[![Current tag](http://img.shields.io/github/tag/yeahwhat-mc/ChestShopLogger.svg)](https://github.com/yeahwhat-mc/ChestShopLogger/tags) [![Repository issues](http://issuestats.com/github/yeahwhat-mc/ChestShopLogger/badge/issue)](http://issuestats.com/github/yeahwhat-mc/ChestShopLogger)

With ChestShopLogger you can log all your ChestShops into a MySQL database. The transaction data, if someone buys oder sells something in a shop, is also logged into the database. You can use this data for your own plugins or for a web interface, for example. Features Log each ChestShop into the database Log transactions into the database Delete logged ChestShops (and the related transactions), after they were removed from.

Credits to @[ColorizedMind](https://github.com/ColorizedMind) for his work on the plugin and the permission to continue maintaining it.

## Features

* Log each ChestShop into the database
* Log transactions into the database
* Delete logged ChestShops (and the related transactions), after they were removed from the map

## Installation

1. Make sure, that the ChestShop plugin is running on your server
2. Upload the plugin files into your plugins directory
3. Restart your server / load the plugin
4. Enter your MySQL access data into the `config.yml`
5. Restart your server / reload the plugin
6. Recreate every ChestShop to initialize them

## Permissions

Permission | Purpose | Default
--- | --- | ---
`chestshoplogger.tp` | Allows a player to teleport to a specific shop | _none_
`chestshoplogger.coords` | Allows a player to view the shop coords | _none_
`chestshoplogger.find` | Allows a player to search for shops | _none_

## Configuration

```yml
general:
  metrics: true
database:
  host: localhost
  port: 3306
  user: new_chestshop
  password: y5y13fxsWCcNzV
  database: new_chestshop
  tableVersion: 2
```

## Development

1. Clone this repository:
  `git clone https://github.com/yeahwhat-mc/ChestShopLogger`
2. Run maven to compile the Java executable:  
  `mvn install`

## Contributing

1. Fork it
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## Requirements / Dependencies

* JDK 7
* Maven
* Bukkit/Spigot

## Version

0.1.8

## License

[GPL 3.0](LICENSE)
