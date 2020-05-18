const { AccessController } = require('orbit-db-access-controllers')

const type = 'othertype'

class BasicController extends AccessController {

  constructor (orbitdb, options = {}) {
    super()
    this._orbitdb = orbitdb
    this._db = null
    this._options = options || {}
  }

  static async create (orbitdb, options) {
    const ac = new BasicController(orbitdb, options);
    return ac;
  }

  static get type () {
    return type
  }

  get address () {
    return this._db.address
  }

  async canAppend(entry, identityProvider) {
    console.log("canAppend?");

    // logic to determine if entry can be added
    return true;
  }

  async grant (access, identity) {
    return true;
  } // Logic for granting access to identity

  async revoke (access, identity) { return false }

  /* AC creation and loading */
  async load (address) {}

  /* Returns AC manifest parameters object */
  async save () {}

  /* Called when the database for this AC gets closed */
  async close () {}

}

module.exports = BasicController
