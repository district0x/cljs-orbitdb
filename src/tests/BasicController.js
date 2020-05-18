const AccessController = require('orbit-db-access-controllers')

class BasicController extends AccessController {

  static get type () { return 'othertype' } // Return the type for this controller

  async canAppend(entry, identityProvider) {
    // logic to determine if entry can be added, for example
    return true
  }

  async grant (access, identity) {} // Logic for granting access to identity
}

console.log("FUBAR");

module.exports = BasicController
