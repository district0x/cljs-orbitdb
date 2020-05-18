const AccessController = require('orbit-db-access-controllers')

class BasicController extends AccessController {

  static get type () {

    // console.log("type");

    return 'othertype';

  } // Return the type for this controller

  async canAppend(entry, identityProvider) {
    console.log("canAppend?");

    // logic to determine if entry can be added
    return true;
  }

  async grant (access, identity) {
      return true;
  } // Logic for granting access to identity
}

module.exports = BasicController
