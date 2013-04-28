/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 3 Feb 2013                                                      -
 *  -                                                                            	-
 *  -       _______.  ______      ___      .__   __.      ___       ______       	-
 *  -      /       | /      |    /   \     |  \ |  |     /   \     /  __  \      	-
 *  -     |   (----`|  ,----'   /  ^  \    |   \|  |    /  ^  \   |  |  |  |     	-
 *  -      \   \    |  |       /  /_\  \   |  . `  |   /  /_\  \  |  |  |  |     	-
 *  -  .----)   |   |  `----. /  _____  \  |  |\   |  /  _____  \ |  `--'  |     	-
 *  -  |_______/     \______|/__/     \__\ |__| \__| /__/     \__\ \______/      	-
 *  -----------------------------------------------------------------------------
 */

package io.nao.scanao.srv

import com.aldebaran.proxy.Variant
import io.nao.scanao.msg.Space.Space
import io.nao.scanao.msg.Hand.Hand
import io.nao.scanao.msg.JointName.JointName
import io.nao.scanao.msg._

trait SNMotion extends SNProxy {
  def angleInterpolation(names: List[Joint], isAbsolute: Boolean) {
    val arrayOfNames = names.map(_.name.toString).toArray
    val vnames: Variant = new Variant(arrayOfNames)
    val arrayOfAngles = names.map(_.angle).toArray
    val arrayOfTimes = names.map(_.time).toArray
    val vangleLists: Variant = new Variant(arrayOfAngles)
    val vtimeLists: Variant = new Variant(arrayOfTimes)
    val visAbsolute: Variant = new Variant(isAbsolute)
    call("angleInterpolation", vnames, vangleLists, vtimeLists, visAbsolute)
  }

  def angleInterpolationBezier(names: List[Joint]) {
    val arrayOfNames = names.map(_.name.toString).toArray
    val vNames: Variant = new Variant(arrayOfNames)
    val arrayOfTimes = names.map(_.time).toArray
    val arrayOfControlPointObject = names.map(_.controlPoint)
    // FlatMap all the structure to get something like [[[1.1, [1, 1.1, 1.1], [1, 1.1, 1.1]]]
    val vcontrolPoints = arrayOfControlPointObject.foldLeft(new Variant())((x, y) => {
      x.push_back(y.get match {
        case c@ControlPoint(_, _, _) => {
          // Build dAngle
          val varPitchAngle: Variant = new Variant(c.angle)
          // Build Handle1
          val varPitchHdl1: Variant = new Variant
          varPitchHdl1.push_back(new Variant(c.handle1.interpolationType))
          varPitchHdl1.push_back(new Variant(c.handle1.angle))
          varPitchHdl1.push_back(new Variant(c.handle1.time))

          // Build Handle2
          val varPitchHdl2: Variant = new Variant
          varPitchHdl2.push_back(new Variant(c.handle2.interpolationType))
          varPitchHdl2.push_back(new Variant(c.handle2.angle))
          varPitchHdl2.push_back(new Variant(c.handle2.time))

          // Build the sub final array
          val subArray: Variant = new Variant
          subArray.push_back(varPitchAngle)
          subArray.push_back(varPitchHdl1)
          subArray.push_back(varPitchHdl2)

          // Build the last Variant to get an array of arrays
          val controlPoints: Variant = new Variant
          controlPoints.push_back(subArray)
          val controlWrap: Variant = new Variant
          controlWrap.push_back(controlPoints)
          controlPoints
        }
      }
      )
      // Return the overall Variant for the next foldLeft
      x
    }
    )

    val time: Variant = new Variant(arrayOfTimes)
    val vtimes: Variant = new Variant
    vtimes.push_back(time)
    // The final array should looks like [ ["HeadYaw"]             , [[1.1   ]],  [ [[1.1, [1, 1.1, 1.1], [1, 1.1, 1.1]],                                      ] ] ]
    //                                  [  ["HeadYaw", "HeadPitch"], [[1  , 1]],  [ [[1,   [0, 0.1, 1  ], [0, 0.1, 1 ]]], [[1  , [0, 0.1, 1  ], [0, 0.1, 1  ]] ] ] ]
    call("angleInterpolationBezier", vNames, vtimes, vcontrolPoints)

  }

  def angleInterpolationWithSpeed(names: List[Joint], maxSpeed: Float) {
    val arrayOfNames = names.map(_.name.toString).toArray
    val vNames: Variant = new Variant(arrayOfNames)
    val arrayOfTimes = names.map(_.angle).toArray
    val vAngles: Variant = new Variant(arrayOfTimes)
    val vMaxSpeed: Variant = new Variant(maxSpeed)

    call("angleInterpolationWithSpeed", vNames, vAngles, vMaxSpeed)
  }

  def areResourcesAvailable(resourceNames: List[Joint]): Boolean = {
    val vresourceNames: Variant = new Variant(resourceNames.map(_.name.toString).toArray)
    val result: Variant = call("areResourcesAvailable", vresourceNames)
    result.toBoolean
  }


  def changeAngles(names: List[Joint], maxSpeed: Float) {
    val arrayOfNames = names.map(_.name.toString).toArray
    val vNames: Variant = new Variant(arrayOfNames)
    val arrayOfTimes = names.map(_.angle).toArray
    val vAngles: Variant = new Variant(arrayOfTimes)
    val vMaxSpeed: Variant = new Variant(maxSpeed)

    call("changeAngles", vNames, vAngles, vMaxSpeed)
  }

  def changePosition(effectorName: Effector, space: Space, fractionMaxSpeed: Float) {
    val veffectorName: Variant = new Variant(effectorName.name)
    val vspace: Variant = new Variant(space.id)
    val vpositionChange: Variant = new Variant(effectorName.position6d.get.toArray())
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    val vaxisMask: Variant = new Variant(effectorName.mask.id)
    call("changePosition", veffectorName, vspace, vpositionChange, vfractionMaxSpeed, vaxisMask)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves an end-effector to the given position and orientation transform. This is a non-blocking call.
  </summary>
  @param chainName Name of the chain. Could be: \"Head\", \"LArm\",\"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param transform Transform arrays
  @param fractionMaxSpeed The fraction of maximum speed to use
  @param axisMask Axis mask. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
    */
  def changeTransform(chainName: String, space: Int, transform: Array[Float], fractionMaxSpeed: Float, axisMask: Int) {
    val vchainName: Variant = new Variant(chainName)
    val vspace: Variant = new Variant(space)
    val vtransform: Variant = new Variant(transform)
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    val vaxisMask: Variant = new Variant(axisMask)
    call("changeTransform", vchainName, vspace, vtransform, vfractionMaxSpeed, vaxisMask)
  }

  //TODO: Change API if needed
  def closeHand(hand: Hand) {
    val vhandName: Variant = new Variant(hand.name)
    call("closeHand", vhandName)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the angles of the joints
  </summary>
  @param names Names the joints, chains, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @param useSensors If true, sensor angles will be returned
  @return Joint angles in radians.
    */
  def getAngles(names: Variant, useSensors: Boolean): Array[Float] = {
    val vnames: Variant = new Variant(names)
    val vuseSensors: Variant = new Variant(useSensors)
    val result: Variant = call("getAngles", vnames, vuseSensors)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the COM of a joint, chain, \"Body\" or \"BodyJoints\".
  </summary>
  @param pName Name of the body which we want the mass. In chain name case, this function give the com of the chain.
  @param pSpace Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param pUseSensorValues If true, the sensor values will be used to determine the position.
  @return The COM position (meter).
    */
  def getCOM(pName: String, pSpace: Int, pUseSensorValues: Boolean): Array[Float] = {
    val vpName: Variant = new Variant(pName)
    val vpSpace: Variant = new Variant(pSpace)
    val vpUseSensorValues: Variant = new Variant(pUseSensorValues)
    val result: Variant = call("getCOM", vpName, vpSpace, vpUseSensorValues)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  // TODO: Change API if needed
  /**
  <summary>
  Allow to know if the collision protection is activated on the given chain.
  </summary>
  @param pChainName The chain name {\"LArm\" or \"RArm\"}.
  @return Return true is the collision protection of the given Arm is activated.
    */
  def getCollisionProtectionEnabled(pChainName: String): Boolean = {
    val vpChainName: Variant = new Variant(pChainName)
    val result: Variant = call("getCollisionProtectionEnabled", vpChainName)
    result.toBoolean
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the foot Gait config (\"MaxStepX\", \"MaxStepY\", \"MaxStepTheta\",  \"MaxStepFrequency\", \"StepHeight\", \"TorsoWx\", \"TorsoWy\")
  </summary>
  @param config a string should be \"Max\", \"Min\", \"Default\"
  @return An ALvalue with the following form :" M a x S t e p X  ", v a l u e ],
    */
  def getFootGaitConfig(config: String): Variant = {
    val vconfig: Variant = new Variant(config)
    val result: Variant = call("getFootGaitConfig", vconfig)
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Get the foot steps. This is a non-blocking call.
  </summary>
  <returns> Give two list of foot steps. The first one give the unchangeable foot step. The second list give the changeable foot steps. Il you use setFootSteps or setFootStepsWithSpeed with clearExisting parmater equal true, walk engine execute unchangeable foot step and remove the other.
    */
  def getFootSteps: Variant = {
    val result: Variant = call("getFootSteps")
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the names of all the joints in the collection.
  </summary>
  @param name Name of a chain, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @return Vector of strings, one for each joint in the collection
    */
  def getJointNames(name: String): Array[String] = {
    val vname: Variant = new Variant(name)
    val result: Variant = call("getJointNames", vname)
    result.toStringArray.asInstanceOf[Array[String]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Get the minAngle (rad), maxAngle (rad), and maxVelocity (rad.s-1) for a given joint or actuator in the body.
  </summary>
  @param name Name of a joint, chain, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @return Array of ALValue arrays containing the minAngle, maxAngle and maxVelocity for all the joints specified.
    */
  def getLimits(name: String): Variant = {
    val vname: Variant = new Variant(name)
    val result: Variant = call("getLimits", vname)
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the mass of a joint, chain, \"Body\" or \"BodyJoints\".
  </summary>
  @param pName Name of the body which we want the mass. \"Body\", \"BodyJoints\" and \"Com\" give the total mass of nao. For the chain, it gives the total mass of the chain.
  @return The mass in kg.
    */
  def getMass(pName: String): Float = {
    val vpName: Variant = new Variant(pName)
    val result: Variant = call("getMass", vpName)
    result.toFloat
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the World Absolute next Robot Position.
  </summary>
  <returns> A vector containing the World Absolute next Robot position.(Absolute Position X, Absolute Position Y, Absolute Angle Z)
    */
  def getNextRobotPosition: Array[Float] = {
    val result: Variant = call("getNextRobotPosition")
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets a Position relative to the TASK_SPACE. Axis definition: the x axis is positive toward Nao's front, the y from right to left and the z is vertical. The angle convention of Position6D is Rot_z(wz).Rot_y(wy).Rot_x(wx).
  </summary>
  @param name Name of the item. Could be: Head, LArm, RArm, LLeg, RLeg, Torso, CameraTop, CameraBottom, MicroFront, MicroRear, MicroLeft, MicroRight, Accelerometer, Gyrometer, Laser, LFsrFR, LFsrFL, LFsrRR, LFsrRL, RFsrFR, RFsrFL, RFsrRR, RFsrRL, USSensor1, USSensor2, USSensor3, USSensor4. Use getSensorNames for the list of sensors supported on your robot.
  @param space Task space {SPACE.Torso = 0, SPACE.World = 1, SPACE.Nao = 2}.
  @param useSensorValues If true, the sensor values will be used to determine the position.
  @return Vector containing the Position6D using meters and radians (x, y, z, wx, wy, wz)
    */
  def getPosition(name: JointName, space: Space, useSensorValues: Boolean): Array[Float] = {
    val vname: Variant = new Variant(name.toString)
    val vspace: Variant = new Variant(space.id)
    val vuseSensorValues: Variant = new Variant(useSensorValues)
    val result: Variant = call("getPosition", vname, vspace, vuseSensorValues)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Get the robot configuration.
  </summary>
  <returns> ALValue arrays containing the robot parameter names and the robot parameter values.
    */
  def getRobotConfig: Variant = {
    val result: Variant = call("getRobotConfig")
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the World Absolute Robot Position.
  </summary>
  @param useSensors If true, use the sensor values
  @return A vector containing the World Absolute Robot Position. (Absolute Position X, Absolute Position Y, Absolute Angle Z)
    */
  def getRobotPosition(useSensors: Boolean): Array[Float] = {
    val vuseSensors: Variant = new Variant(useSensors)
    val result: Variant = call("getRobotPosition", vuseSensors)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the World Absolute Robot Velocity.
  </summary>
  <returns> A vector containing the World Absolute Robot Velocity. (Absolute Velocity Translation X [m.s-1], Absolute Velocity Translation Y[m.s-1], Absolute Velocity Rotation WZ [rd.s-1])
    */
  def getRobotVelocity: Array[Float] = {
    val result: Variant = call("getRobotVelocity")
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets the list of sensors supported on your robot.
  </summary>
  <returns> Vector of sensor names
    */
  def getSensorNames: Array[String] = {
    val result: Variant = call("getSensorNames")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets stiffness of a joint or group of joints
  </summary>
  @param jointName Name of the joints, chains, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @return One or more stiffnesses. 1.0 indicates maximum stiffness. 0.0 indicated minimum stiffness
    */
  def getStiffnesses(jointName: Variant): Array[Float] = {
    val vjointName: Variant = new Variant(jointName)
    val result: Variant = call("getStiffnesses", vjointName)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  Returns a string representation of the Model's state
  </summary>
  <returns> A formated string
    */
  def getSummary: String = {
    val result: Variant = call("getSummary")
    result.toString
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets an ALValue structure describing the tasks in the Task List
  </summary>
  <returns> An ALValue containing an ALValue for each task. The inner ALValue contains: Name, MotionID
    */
  def getTaskList: Variant = {
    val result: Variant = call("getTaskList")
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets an Homogenous Transform relative to the TASK_SPACE. Axis definition: the x axis is positive toward Nao's front, the y from right to left and the z is vertical.
  </summary>
  @param name Name of the item. Could be: any joint or chain or sensor (Head, LArm, RArm, LLeg, RLeg, Torso, HeadYaw, ..., CameraTop, CameraBottom, MicroFront, MicroRear, MicroLeft, MicroRight, Accelerometer, Gyrometer, Laser, LFsrFR, LFsrFL, LFsrRR, LFsrRL, RFsrFR, RFsrFL, RFsrRR, RFsrRL, USSensor1, USSensor2, USSensor3, USSensor4. Use getSensorNames for the list of sensors supported on your robot.
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param useSensorValues If true, the sensor values will be used to determine the position.
  @return Vector of 16 floats corresponding to the values of the matrix, line by line.
    */
  def getTransform(name: String, space: Int, useSensorValues: Boolean): Array[Float] = {
    val vname: Variant = new Variant(name)
    val vspace: Variant = new Variant(space)
    val vuseSensorValues: Variant = new Variant(useSensorValues)
    val result: Variant = call("getTransform", vname, vspace, vuseSensorValues)
    result.toFloatArray.asInstanceOf[Array[Float]]
  }

  //TODO: Change API if needed
  /**
  <summary>
  DEPRECATED method.
  </summary>
  <returns> True Arm Motions are controlled by the Walk Task.
    */
  def getWalkArmsEnable: Variant = {
    val result: Variant = call("getWalkArmsEnable")
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Gets if Arms Motions are enabled during the Walk Process.
  </summary>
  <returns> True Arm Motions are controlled by the Walk Task.
    */
  def getWalkArmsEnabled: Variant = {
    val result: Variant = call("getWalkArmsEnabled")
    result
  }

  //TODO: Change API if needed
  /**
  <summary>
  Give the collision state of a chain. If a chain has a collision state \"none\" or \"near\", it could be desactivated.
  </summary>
  @param pChainName The chain name {\"Arms\", \"LArm\" or \"RArm\"}.
  @return A string which notice the collision state: \"none\" there are no collision, \"near\" the collision is taking in account in the anti-collision algorithm, \"collision\" the chain is in contact with an other body. If the chain asked is \"Arms\" the most unfavorable result is given.
    */
  def isCollision(pChainName: String): String = {
    val vpChainName: Variant = new Variant(pChainName)
    val result: Variant = call("isCollision", vpChainName)
    result.toString
  }

  //TODO: Change API if needed
  /**
  <summary>
  Kills a motion task.
  </summary>
  @param motionTaskID TaskID of the motion task you want to kill.
  @return Return true if the specified motionTaskId has been killed.
    */
  def killTask(motionTaskID: Int): Boolean = {
    val vmotionTaskID: Variant = new Variant(motionTaskID)
    val result: Variant = call("killTask", vmotionTaskID)
    result.toBoolean
  }

  //TODO: Change API if needed
  /**
  <summary>
  Kills all tasks that use any of the resources given. Only motion API's' blocking call takes resources and can be killed. Use getJointNames(\"Body\") to have the list of the available joint for your robot.
  </summary>
  @param resourceNames A vector of resource joint names
    */
  def killTasksUsingResources(resourceNames: Array[String]) {
    val vresourceNames: Variant = new Variant(resourceNames)
    call("killTasksUsingResources", vresourceNames)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Emergency Stop on Walk task: This method will end the walk task brutally, without attempting to return to a balanced state. If Nao has one foot in the air, he could easily fall.
  </summary>
    */
  def killWalk() {
    call("killWalk")
  }

  //TODO: Change API if needed
  def openHand(hand: Hand) {
    val vhandName: Variant = new Variant(hand.name)
    call("openHand", vhandName)
  }

  //TODO: Change API if needed
  //TODO: Change API if needed
  /**
  <summary>
  Moves an end-effector to the given position and orientation over time. This is a blocking call.
  </summary>
  @param chainName Name of the chain. Could be: \"Head\", \"LArm\", \"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param path Vector of 6D position arrays (x,y,z,wx,wy,wz) in meters and radians
  @param axisMask Axis mask. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
  @param durations Vector of times in seconds corresponding to the path points
  @param isAbsolute If true, the movement is absolute else relative
    */
  def positionInterpolation(chainName: String, space: Int, path: Variant, axisMask: Int, durations: Variant, isAbsolute: Boolean) {
    val vchainName: Variant = new Variant(chainName)
    val vspace: Variant = new Variant(space)
    val vpath: Variant = new Variant(path)
    val vaxisMask: Variant = new Variant(axisMask)
    val vdurations: Variant = new Variant(durations)
    val visAbsolute: Variant = new Variant(isAbsolute)
    call("positionInterpolation", vchainName, vspace, vpath, vaxisMask, vdurations, visAbsolute)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves end-effectors to the given positions and orientations over time. This is a blocking call.
  </summary>
  @param effectorNames Vector of chain names. Could be: \"Head\", \"LArm\", \"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param taskSpaceForAllPaths Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param paths Vector of 6D position arrays (x,y,z,wx,wy,wz) in meters and radians
  @param axisMasks Vector of Axis Masks. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
  @param relativeTimes Vector of times in seconds corresponding to the path points
  @param isAbsolute If true, the movement is absolute else relative
    */
  def positionInterpolations(effectorNames: Array[String], taskSpaceForAllPaths: Int, paths: Variant, axisMasks: Variant, relativeTimes: Variant, isAbsolute: Boolean) {
    val veffectorNames: Variant = new Variant(effectorNames)
    val vtaskSpaceForAllPaths: Variant = new Variant(taskSpaceForAllPaths)
    val vpaths: Variant = new Variant(paths)
    val vaxisMasks: Variant = new Variant(axisMasks)
    val vrelativeTimes: Variant = new Variant(relativeTimes)
    val visAbsolute: Variant = new Variant(isAbsolute)
    call("positionInterpolations", veffectorNames, vtaskSpaceForAllPaths, vpaths, vaxisMasks, vrelativeTimes, visAbsolute)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Sets angles. This is a non-blocking call.
  </summary>
  @param names The name or names of joints, chains, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @param angles One or more angles in radians
  @param fractionMaxSpeed The fraction of maximum speed to use
    */
  def setAngles(names: Variant, angles: Variant, fractionMaxSpeed: Float) {
    val vnames: Variant = new Variant(names)
    val vangles: Variant = new Variant(angles)
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    call("setAngles", vnames, vangles, vfractionMaxSpeed)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Enable Anticollision protection of the arms of the robot. Use api isCollision to know if a chain is in collision and can be disactivated.
  </summary>
  @param pChainName The chain name {\"Arms\", \"LArm\" or \"RArm\"}.
  @param pEnable Activate or disactivate the anticollision of the desired Chain.
  @return A bool which notice if the desired activation/disactivation is applyed successfully. Activation always success. Disactivation success only if the current state of the chain is not in collision. If we want to disactivate collision of the both arms (\"Arms\"), the two arms must be without collision.
    */
  def setCollisionProtectionEnabled(pChainName: String, pEnable: Boolean): Boolean = {
    val vpChainName: Variant = new Variant(pChainName)
    val vpEnable: Variant = new Variant(pEnable)
    val result: Variant = call("setCollisionProtectionEnabled", vpChainName, vpEnable)
    result.toBoolean
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao do foot step planner. This is a non-blocking call.
  </summary>
  @param legName name of the leg to move('LLeg'or 'RLeg')
  @param footSteps [x, y, theta], [Position along X/Y, Orientation round Z axis] of the leg relative to the other Leg in [meters, meters, radians]. Must be less than [MaxStepX, MaxStepY, MaxStepTheta]
  @param timeList time list of each foot step
  @param clearExisting Clear existing foot steps.
    */
  def setFootSteps(legName: Array[String], footSteps: Variant, timeList: Array[Float], clearExisting: Boolean) {
    val vlegName: Variant = new Variant(legName)
    val vfootSteps: Variant = new Variant(footSteps)
    val vtimeList: Variant = new Variant(timeList)
    val vclearExisting: Variant = new Variant(clearExisting)
    call("setFootSteps", vlegName, vfootSteps, vtimeList, vclearExisting)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao do foot step planner with speed. This is a blocking call.
  </summary>
  @param legName name of the leg to move('LLeg'or 'RLeg')
  @param footSteps [x, y, theta], [Position along X/Y, Orientation round Z axis] of the leg relative to the other Leg in [meters, meters, radians]. Must be less than [MaxStepX, MaxStepY, MaxStepTheta]
  @param fractionMaxSpeed speed of each foot step. Must be between 0 and 1.
  @param clearExisting Clear existing foot steps.
    */
  def setFootStepsWithSpeed(legName: Array[String], footSteps: Variant, fractionMaxSpeed: Array[Float], clearExisting: Boolean) {
    val vlegName: Variant = new Variant(legName)
    val vfootSteps: Variant = new Variant(footSteps)
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    val vclearExisting: Variant = new Variant(clearExisting)
    call("setFootStepsWithSpeed", vlegName, vfootSteps, vfractionMaxSpeed, vclearExisting)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Internal Use.
  </summary>
  @param config Internal: An array of ALValues [i][0]: name, [i][1]: value
    */
  def setMotionConfig(config: Variant) {
    val vconfig: Variant = new Variant(config)
    call("setMotionConfig", vconfig)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves an end-effector to the given position and orientation. This is a non-blocking call.
  </summary>
  @param chainName Name of the chain. Could be: \"Head\", \"LArm\",\"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param position 6D position array (x,y,z,wx,wy,wz) in meters and radians
  @param fractionMaxSpeed The fraction of maximum speed to use
  @param axisMask Axis mask. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
    */
  def setPosition(chainName: String, space: Int, position: Array[Float], fractionMaxSpeed: Float, axisMask: Int) {
    val vchainName: Variant = new Variant(chainName)
    val vspace: Variant = new Variant(space)
    val vposition: Variant = new Variant(position)
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    val vaxisMask: Variant = new Variant(axisMask)
    call("setPosition", vchainName, vspace, vposition, vfractionMaxSpeed, vaxisMask)
  }

  //TODO: Change API if needed
  def setStiffnesses(joint: H25) {
    val vnames: Variant = new Variant(joint.name)
    val vstiffnesses: Variant = new Variant(joint.stiffness)
    call("setStiffnesses", vnames, vstiffnesses)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves an end-effector to the given position and orientation transform. This is a non-blocking call.
  </summary>
  @param chainName Name of the chain. Could be: \"Head\", \"LArm\",\"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param transform Transform arrays
  @param fractionMaxSpeed The fraction of maximum speed to use
  @param axisMask Axis mask. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
    */
  def setTransform(chainName: String, space: Int, transform: Array[Float], fractionMaxSpeed: Float, axisMask: Int) {
    val vchainName: Variant = new Variant(chainName)
    val vspace: Variant = new Variant(space)
    val vtransform: Variant = new Variant(transform)
    val vfractionMaxSpeed: Variant = new Variant(fractionMaxSpeed)
    val vaxisMask: Variant = new Variant(axisMask)
    call("setTransform", vchainName, vspace, vtransform, vfractionMaxSpeed, vaxisMask)
  }

  //TODO: Change API if needed
  /**
  <summary>
  DEPRECATED method.
  </summary>
  @param leftArmEnable if true Left Arm motions are controlled by the Walk Task
  @param rightArmEnable if true Right Arm mMotions are controlled by the Walk Task
    */
  def setWalkArmsEnable(leftArmEnable: Boolean, rightArmEnable: Boolean) {
    val vleftArmEnable: Variant = new Variant(leftArmEnable)
    val vrightArmEnable: Variant = new Variant(rightArmEnable)
    call("setWalkArmsEnable", vleftArmEnable, vrightArmEnable)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Sets if Arms Motions are enabled during the Walk Process.
  </summary>
  @param leftArmEnable if true Left Arm motions are controlled by the Walk Task
  @param rightArmEnable if true Right Arm mMotions are controlled by the Walk Task
    */
  def setWalkArmsEnabled(leftArmEnable: Boolean, rightArmEnable: Boolean) {
    val vleftArmEnable: Variant = new Variant(leftArmEnable)
    val vrightArmEnable: Variant = new Variant(rightArmEnable)
    call("setWalkArmsEnabled", vleftArmEnable, vrightArmEnable)
  }

  //TODO: Change API if needed
  /**
  <summary>
  </summary>
  @param x Fraction of MaxStepX. Use negative for backwards. [-1.0 to 1.0]
  @param y Fraction of MaxStepY. Use negative for right. [-1.0 to 1.0]
  @param theta Fraction of MaxStepTheta. Use negative for clockwise [-1.0 to 1.0]
  @param frequency Fraction of MaxStepFrequency [0.0 to 1.0]
    */
  def setWalkTargetVelocity(x: Float, y: Float, theta: Float, frequency: Float) {
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    val vfrequency: Variant = new Variant(frequency)
    call("setWalkTargetVelocity", vx, vy, vtheta, vfrequency)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk at the given velocity. This is a non-blocking call.
  </summary>
  @param x Fraction of MaxStepX. Use negative for backwards. [-1.0 to 1.0]
  @param y Fraction of MaxStepY. Use negative for right. [-1.0 to 1.0]
  @param theta Fraction of MaxStepTheta. Use negative for clockwise [-1.0 to 1.0]
  @param frequency Fraction of MaxStepFrequency [0.0 to 1.0]
  @param feetGaitConfig An ALValue with the custom gait configuration for both feet
    */
  def setWalkTargetVelocity(x: Float, y: Float, theta: Float, frequency: Float, feetGaitConfig: Variant) {
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    val vfrequency: Variant = new Variant(frequency)
    val vfeetGaitConfig: Variant = new Variant(feetGaitConfig)
    call("setWalkTargetVelocity", vx, vy, vtheta, vfrequency, vfeetGaitConfig)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk at the given velocity. This is a non-blocking call.
  </summary>
  @param x Fraction of MaxStepX. Use negative for backwards. [-1.0 to 1.0]
  @param y Fraction of MaxStepY. Use negative for right. [-1.0 to 1.0]
  @param theta Fraction of MaxStepTheta. Use negative for clockwise [-1.0 to 1.0]
  @param frequency Fraction of MaxStepFrequency [0.0 to 1.0]
  @param leftFootGaitConfig An ALValue with custom gait configuration for the left foot
  @param rightFootGaitConfig An ALValue with custom gait configuration for the right foot
    */
  def setWalkTargetVelocity(x: Float, y: Float, theta: Float, frequency: Float, leftFootGaitConfig: Variant, rightFootGaitConfig: Variant) {
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    val vfrequency: Variant = new Variant(frequency)
    val vleftFootGaitConfig: Variant = new Variant(leftFootGaitConfig)
    val vrightFootGaitConfig: Variant = new Variant(rightFootGaitConfig)
    call("setWalkTargetVelocity", vx, vy, vtheta, vfrequency, vleftFootGaitConfig, vrightFootGaitConfig)
  }

  //TODO: Change API if needed
  /**
  <summary>
  DEPRECATED Please used new api footStepsWithSpeed.
  </summary>
  @param legName name of the leg to move('LLeg'or 'RLeg')
  @param x Position along X axis of the leg relative to the other Leg in meters. Must be less than MaxStepX
  @param y Position along Y axis of the leg relative to the other Leg in meters. Must be less than MaxStepY
  @param theta Orientation round Z axis of the leg relative to the other leg in radians. Must be less than MaxStepX
    */
  def stepTo(legName: String, x: Float, y: Float, theta: Float) {
    val vlegName: Variant = new Variant(legName)
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    call("stepTo", vlegName, vx, vy, vtheta)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Interpolates one or multiple joints to a target stiffness or along timed trajectories of stiffness. This is a blocking call.
  </summary>
  @param names Name or names of joints, chains, \"Body\", \"BodyJoints\" or \"BodyActuators\".
  @param stiffnessLists An stiffness, list of stiffnesses or list of list of stiffnesses
  @param timeLists A time, list of times or list of list of times.
    */
  def stiffnessInterpolation(names: Variant, stiffnessLists: Variant, timeLists: Variant) {
    val vnames: Variant = new Variant(names)
    val vstiffnessLists: Variant = new Variant(stiffnessLists)
    val vtimeLists: Variant = new Variant(timeLists)
    call("stiffnessInterpolation", vnames, vstiffnessLists, vtimeLists)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Stop Walk task at next double support: This method will end the walk task less brutally than killWalk but more quickly than setWalkTargetVelocity(0.0, 0.0, 0.0, pFrequency).
  </summary>
    */
  def stopWalk() {
    call("stopWalk")
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves an end-effector to the given position and orientation over time using homogenous transforms to describe the positions or changes. This is a blocking call.
  </summary>
  @param chainName Name of the chain. Could be: \"Head\", \"LArm\",\"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param space Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param path Vector of Transform arrays
  @param axisMask Axis mask. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
  @param duration Vector of times in seconds corresponding to the path points
  @param isAbsolute If true, the movement is absolute else relative
    */
  def transformInterpolation(chainName: String, space: Int, path: Variant, axisMask: Int, duration: Variant, isAbsolute: Boolean) {
    val vchainName: Variant = new Variant(chainName)
    val vspace: Variant = new Variant(space)
    val vpath: Variant = new Variant(path)
    val vaxisMask: Variant = new Variant(axisMask)
    val vduration: Variant = new Variant(duration)
    val visAbsolute: Variant = new Variant(isAbsolute)
    call("transformInterpolation", vchainName, vspace, vpath, vaxisMask, vduration, visAbsolute)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Moves end-effector to the given transforms over time. This is a blocking call.
  </summary>
  @param effectorNames Vector of chain names. Could be: \"Head\", \"LArm\", \"RArm\", \"LLeg\", \"RLeg\", \"Torso\"
  @param taskSpaceForAllPaths Task space {SPACE_TORSO = 0, SPACE_WORLD = 1, SPACE_NAO = 2}.
  @param paths Vector of transforms arrays.
  @param axisMasks Vector of Axis Masks. True for axes that you wish to control. e.g. 7 for position only, 56 for rotation only and 63 for both
  @param relativeTimes Vector of times in seconds corresponding to the path points
  @param isAbsolute If true, the movement is absolute else relative
    */
  def transformInterpolations(effectorNames: Array[String], taskSpaceForAllPaths: Int, paths: Variant, axisMasks: Variant, relativeTimes: Variant, isAbsolute: Boolean) {
    val veffectorNames: Variant = new Variant(effectorNames)
    val vtaskSpaceForAllPaths: Variant = new Variant(taskSpaceForAllPaths)
    val vpaths: Variant = new Variant(paths)
    val vaxisMasks: Variant = new Variant(axisMasks)
    val vrelativeTimes: Variant = new Variant(relativeTimes)
    val visAbsolute: Variant = new Variant(isAbsolute)
    call("transformInterpolations", veffectorNames, vtaskSpaceForAllPaths, vpaths, vaxisMasks, vrelativeTimes, visAbsolute)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Update the target to follow by the head of NAO.
  </summary>
  @param pTargetPositionWy The target position wy in SPACE_NAO
  @param pTargetPositionWz The target position wz in  SPACE_NAO
  @param pTimeSinceDetectionMs The time in Ms since the target was detected
  @param pUseOfWholeBody If true, the target is follow in cartesian space by the Head with whole Body constraints.
    */
  def updateTrackerTarget(pTargetPositionWy: Float, pTargetPositionWz: Float, pTimeSinceDetectionMs: Int, pUseOfWholeBody: Boolean) {
    val vpTargetPositionWy: Variant = new Variant(pTargetPositionWy)
    val vpTargetPositionWz: Variant = new Variant(pTargetPositionWz)
    val vpTimeSinceDetectionMs: Variant = new Variant(pTimeSinceDetectionMs)
    val vpUseOfWholeBody: Variant = new Variant(pUseOfWholeBody)
    call("updateTrackerTarget", vpTargetPositionWy, vpTargetPositionWz, vpTimeSinceDetectionMs, vpUseOfWholeBody)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Waits until the WalkTask is finished: This method can be used to block your script/code execution until the walk task is totally finished.
  </summary>
    */
  def waitUntilWalkIsFinished() {
    call("waitUntilWalkIsFinished")
  }

  //TODO: Change API if needed
  /**
  <summary>
  Initialize the walk process. Check the robot pose and take a right posture.This is blocking called.
  </summary>
    */
  def walkInit() {
    call("walkInit")
  }

  //TODO: Change API if needed
  /**
  <summary>
  Return True if Walk is Active.
  </summary>
  <returns>
    */
  def walkIsActive: Boolean = {
    val result = call("walkIsActive")
    result.toBoolean
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk to the given relative Position. This is a blocking call.
  </summary>
  @param x Distance along the X axis in meters.
  @param y Distance along the Y axis in meters.
  @param theta Rotation around the Z axis in radians [-3.1415 to 3.1415].
    */
  def walkTo(x: Float, y: Float, theta: Float) {
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    call("walkTo", vx, vy, vtheta)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk to the given relative Position with custom gait parmaters.
  </summary>
  @param x Distance along the X axis in meters.
  @param y Distance along the Y axis in meters.
  @param theta Rotation around the Z axis in radians [-3.1415 to 3.1415].
  @param feetGaitConfig An ALValue with the custom gait configuration for both feet.
    */
  def walkTo(x: Float, y: Float, theta: Float, feetGaitConfig: Variant) {
    val vx: Variant = new Variant(x)
    val vy: Variant = new Variant(y)
    val vtheta: Variant = new Variant(theta)
    val vfeetGaitConfig: Variant = new Variant(feetGaitConfig)
    call("walkTo", vx, vy, vtheta, vfeetGaitConfig)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk to the given relative Position. This is a blocking call.
  </summary>
  @param controlPoint An ALValue with all the control point [x1, y 1, t h e t a 1 ],..., [ x N, y N, t h e t a N ]
    */
  def walkTo(controlPoint: Variant) {
    val vcontrolPoint: Variant = new Variant(controlPoint)
    call("walkTo", vcontrolPoint)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Makes Nao walk to the given relative Position. This is a blocking call.
  </summary>
  @param controlPoint An ALValue with all the control point [x1, y 1, t h e t a 1 ],..., [ x N, y N, t h e t a N ]
  @param feetGaitConfig An ALValue with the custom gait configuration for both feet
    */
  def walkTo(controlPoint: Variant, feetGaitConfig: Variant) {
    val vcontrolPoint: Variant = new Variant(controlPoint)
    val vfeetGaitConfig: Variant = new Variant(feetGaitConfig)
    call("walkTo", vcontrolPoint, vfeetGaitConfig)
  }

  //TODO: Change API if needed
  /**
  <summary>
  UserFriendly Whole Body API: enable Whole Body Balancer. It's a Generalized Inverse Kinematics which deals with cartesian control, balance, redundancy and task priority. The main goal is to generate and stabilized consistent motions without precomputed trajectories and adapt nao's behaviour to the situation. The generalized inverse kinematic problem takes in account equality constraints (keep foot fix), inequality constraints (joint limits, balance, ...) and quadratic minimization (cartesian / articular desired trajectories). We solve each step a quadratic programming on the robot.
  </summary>
  @param isEnabled Active / Disactive Whole Body Balancer.
    */
  def wbEnable(isEnabled: Boolean) {
    val visEnabled: Variant = new Variant(isEnabled)
    call("wbEnable", visEnabled)
  }

  //TODO: Change API if needed
  /**
  <summary>
  UserFriendly Whole Body API: enable to keep balance in support polygon.
  </summary>
  @param isEnable Enable Nao to keep balance.
  @param supportLeg Name of the support leg: \"Legs\", \"LLeg\", \"RLeg\".
    */
  def wbEnableBalanceConstraint(isEnable: Boolean, supportLeg: String) {
    val visEnable: Variant = new Variant(isEnable)
    val vsupportLeg: Variant = new Variant(supportLeg)
    call("wbEnableBalanceConstraint", visEnable, vsupportLeg)
  }

  //TODO: Change API if needed
  /**
  <summary>
  UserFriendly Whole Body API: enable whole body cartesian control of an effector.
  </summary>
  @param effectorName Name of the effector : \"Head\", \"LArm\" or \"RArm\". Nao goes to posture init. He manages his balance and keep foot fix. \"Head\" is controlled in rotation. \"LArm\" and \"RArm\" are controlled in position.
  @param isEnabled Active / Disactive Effector Control.
    */
  def wbEnableEffectorControl(effectorName: String, isEnabled: Boolean) {
    val veffectorName: Variant = new Variant(effectorName)
    val visEnabled: Variant = new Variant(isEnabled)
    call("wbEnableEffectorControl", veffectorName, visEnabled)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Advanced Whole Body API: enable to control an effector as an optimization.
  </summary>
  @param effectorName Name of the effector : \"All\", \"Arms\", \"Legs\", \"Head\", \"LArm\", \"RArm\", \"LLeg\", \"RLeg\", \"Torso\", \"Com\".
  @param isActive if true, the effector control is taken in acount in the optimization criteria.
    */
  def wbEnableEffectorOptimization(effectorName: String, isActive: Boolean) {
    val veffectorName: Variant = new Variant(effectorName)
    val visActive: Variant = new Variant(isActive)
    call("wbEnableEffectorOptimization", veffectorName, visActive)
  }

  //TODO: Change API if needed
  /**
  <summary>
  UserFriendly Whole Body API: set the foot state: fixed foot, constrained in a plane or free.
  </summary>
  @param stateName Name of the foot state. \"Fixed\" set the foot fixed. \"Plane\" constrained the Foot in the plane. \"Free\" set the foot free.
  @param supportLeg Name of the foot. \"LLeg\", \"RLeg\" or \"Legs\".
    */
  def wbFootState(stateName: String, supportLeg: String) {
    val vstateName: Variant = new Variant(stateName)
    val vsupportLeg: Variant = new Variant(supportLeg)
    call("wbFootState", vstateName, vsupportLeg)
  }

  //TODO: Change API if needed
  /**
  <summary>
  Advanced Whole Body API: \"Com\" go to a desired support polygon. This is a blocking call.
  </summary>
  @param supportLeg Name of the support leg: \"Legs\", \"LLeg\", \"RLeg\".
  @param duration Time in seconds.
    */
  def wbGoToBalance(supportLeg: String, duration: Float) {
    val vsupportLeg: Variant = new Variant(supportLeg)
    val vduration: Variant = new Variant(duration)
    call("wbGoToBalance", vsupportLeg, vduration)
  }

  //TODO: Change API if needed
  /**
  <summary>
  UserFriendly Whole Body API: set new target for controlled effector. This is a non-blocking call.
  </summary>
  @param effectorName Name of the effector : \"Head\", \"LArm\" or \"RArm\". Nao goes to posture init. He manages his balance and keep foot fix. \"Head\" is controlled in rotation. \"LArm\" and \"RArm\" are controlled in position.
  @param targetCoordinate \"Head\" is controlled in rotation (WX, WY, WZ). \"LArm\" and \"RArm\" are controlled in position (X, Y, Z). TargetCoordinate must be absolute and expressed in SPACE_NAO. If the desired position/orientation is unfeasible, target is resize to the nearest feasible motion.
    */
  def wbSetEffectorControl(effectorName: String, targetCoordinate: Variant) {
    val veffectorName: Variant = new Variant(effectorName)
    val vtargetCoordinate: Variant = new Variant(targetCoordinate)
    call("wbSetEffectorControl", veffectorName, vtargetCoordinate)
  }
}

