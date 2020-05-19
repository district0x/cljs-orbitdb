let OrbitDB = require('orbit-db')
let AccessControllers = require('orbit-db-access-controllers')
const AccessController = require('orbit-db-access-controllers/src/access-controller-interface')
const IpfsClient = require('ipfs-http-client')

const ipfs = IpfsClient ("http://localhost:5001")

class OtherAccessController1 extends AccessController {

  static get type () { return 'othertype' } // Return the type for this controller

  async canAppend(entry, identityProvider) {
    // logic to determine if entry can be added, for example:
    // if (entry.payload === "hello world")
    //   return true

    // return false

    console.log ("can I append? " + entry)

    return true

  }

  async grant (access, identity) {} // Logic for granting access to identity

  async save () {
    // return parameters needed for loading
    return { parameter: 'some-parameter-needed-for-loading' }
  }

  static async create (orbitdb, options) {
    return new OtherAccessController1()
  }
}

// NOTE you can set async / get and other sugar with https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty

function OtherAccessController2() {}

(async () => {

  OtherAccessController2.prototype = Object.create(AccessController.prototype)

  OtherAccessController2.prototype.constructor = OtherAccessController2

  OtherAccessController2.type = "othertype"

  OtherAccessController2.create = function (orbitdb, options) {
    return new OtherAccessController2()
  }

  OtherAccessController2.prototype.grant = function (access, identity) {  }

  OtherAccessController2.prototype.save = function () {
    return {} //{ parameter: 'some-parameter-needed-for-loading' }
  }

  OtherAccessController2.prototype.canAppend = function (entry, identityProvider) {
    console.log ("OtherAccessController2 : can I append? ", entry)
    return true
  }

  AccessControllers.addAccessController({ AccessController: OtherAccessController2 })

  // Student.prototype = Object.create(Person.prototype);  // correct the constructor pointer because it points to Person Student.prototype.constructor = Student;

  console.log ("OtherAccessController1.prototype" )

  const orbitdb = await OrbitDB.createInstance(ipfs, {
    AccessControllers: AccessControllers
  })

  const db = await orbitdb.keyvalue('first-database', {
    accessController: {
      type: 'othertype',
      // write: [id1.id]
    }
  })

  await db.set('hello', { name: 'Friend' })
  const value = db.get('hello')

  console.log ("@@@ AFTER " + value)

})();
