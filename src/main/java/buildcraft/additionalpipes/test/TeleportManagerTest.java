package buildcraft.additionalpipes.test;

import java.util.ArrayList;
import java.util.UUID;

import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport.States;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportItems;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.Log;

/**
 * This class is used to make sure the new teleport manager is working properly.
 * @author Jamie
 *
 */
public class TeleportManagerTest
{
	
	public static void runAllTests()
	{
	
		Log.info("[TeleportManagerTest] Testing Basic Adding and Removing");
		
		if(testBasicAddRemove())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing handling of duplicate pipes");
		if(testDuplicateRemoval())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing basic getConnectedPipes()");
		if(testGetConnectedPipesBasic())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing proper behavior of getConnectedPipes() on send-receive pipes");
		if(testGetConnectedPipesSendReceive())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing a real use case for getConnectedPipes()");
		if(testGetConnectedPipesRealUseCase())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		//clean up
		TeleportManager.instance.reset();
	}
	
	public static boolean testBasicAddRemove()
	{
		//just test to make sure no exceptions are thrown
		try
		{
			TeleportManager.instance.reset();
			PipeBehaviorTeleportItems pipe = new PipeBehaviorTeleportItems(null);
			
			pipe.setFrequency(3);
			TeleportManager.instance.add(pipe, pipe.getFrequency());
			
			TeleportManager.instance.remove(pipe, pipe.getFrequency());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
			
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean testGetConnectedPipesBasic()
	{
		TeleportManager.instance.reset();
		PipeBehaviorTeleportItems pipe1 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe2 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe3 = new PipeBehaviorTeleportItems(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		
		pipe1.setState(States.SEND_AND_RECEIVE);;
		pipe2.setState(States.SEND_AND_RECEIVE);;
		pipe3.setState(States.SEND_AND_RECEIVE);;
		
		pipe1.setOwnerUUID(UUID.randomUUID());
		pipe2.setOwnerUUID(UUID.randomUUID());
		pipe3.setOwnerUUID(UUID.randomUUID());
		
		pipe1.setPublic(true);
		pipe2.setPublic(true);
		pipe3.setPublic(true);

		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		
		ArrayList<PipeBehaviorTeleportItems> pipesList = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, true, true);
		
		if(!(pipesList.size() == 2 && (pipesList.contains(pipe2) && pipesList.contains(pipe3))))
		{
			return false;
		}

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);		
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean testDuplicateRemoval()
	{
		TeleportManager.instance.reset();
		PipeBehaviorTeleportItems pipe1 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe2 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe3 = new PipeBehaviorTeleportItems(null);


		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		
		pipe1.setState(States.SEND_AND_RECEIVE);
		pipe2.setState(States.SEND_AND_RECEIVE);
		pipe3.setState(States.SEND_AND_RECEIVE);
		
		pipe1.setOwnerUUID(UUID.randomUUID());
		pipe2.setOwnerUUID(pipe1.getOwnerUUID());
		pipe3.setOwnerUUID(pipe2.getOwnerUUID());

		pipe1.setPublic(true);
		pipe2.setPublic(true);
		pipe3.setPublic(true);

		pipe3.setPipeUUID(pipe2.getPipeUUID()); // Muahahahah!  I am a glitched duplicate, bet you can't remove me!

		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe3, 3);
		TeleportManager.instance.add(pipe2, 3);
		
		ArrayList<PipeBehaviorTeleportItems> pipesList = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, true, true);

		if(!(pipesList.size() == 1 && (pipesList.contains(pipe2))))
		{
			return false;
		}

		TeleportManager.instance.remove(pipe2, 3);

		pipesList = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, true, true);
		if(!(pipesList.size() == 0))
		{
			return false;
		}
		
		TeleportManager.instance.remove(pipe1, 3);	
		return true;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean testGetConnectedPipesSendReceive()
	{
		TeleportManager.instance.reset();
		PipeBehaviorTeleportItems pipe1 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe2 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe3 = new PipeBehaviorTeleportItems(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		
		pipe1.setState(States.SEND_AND_RECEIVE);
		pipe2.setState(States.SEND);
		pipe3.setState(States.RECEIVE);
		
		pipe1.setOwnerUUID(UUID.randomUUID());
		pipe2.setOwnerUUID(UUID.randomUUID());
		pipe3.setOwnerUUID(UUID.randomUUID());

		pipe1.setPublic(true);
		pipe2.setPublic(true);
		pipe3.setPublic(true);

		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		
		//get a list of pipes which can send to pipe1
		ArrayList<PipeBehaviorTeleportItems> sendablePipes = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, true, false);
		
		if(!(sendablePipes.size() == 1 && (sendablePipes.contains(pipe2) && !(sendablePipes.contains(pipe1) && sendablePipes.contains(pipe3)))))
		{
			return false;
		}
		
		//get a list of pipes which pipe1 can send to
		ArrayList<PipeBehaviorTeleportItems> receivingPipes = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, false, true);
		
		if(!(receivingPipes.size() == 1 && (receivingPipes.contains(pipe3) && !(receivingPipes.contains(pipe1) && receivingPipes.contains(pipe2)))))
		{
			return false;
		}

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);		
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean testGetConnectedPipesRealUseCase()
	{
		TeleportManager.instance.reset();
		PipeBehaviorTeleportItems pipe1 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe2 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe3 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe4 = new PipeBehaviorTeleportItems(null);
		PipeBehaviorTeleportItems pipe5 = new PipeBehaviorTeleportItems(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		pipe4.setFrequency(3);
		pipe5.setFrequency(7);
		
		pipe1.setState(States.SEND);  //send only
		pipe2.setState(States.SEND_AND_RECEIVE);  //send and receive
		pipe3.setState(States.RECEIVE); //receive only
		pipe4.setState(States.SEND);  //send only
		pipe5.setState(States.SEND_AND_RECEIVE);  //send and receive

		pipe1.setPublic(true);
		pipe2.setPublic(true);
		pipe3.setPublic(true);
		pipe4.setPublic(true);
		pipe5.setPublic(true);
		
		pipe1.setOwnerUUID(UUID.randomUUID());
		pipe2.setOwnerUUID(UUID.randomUUID());
		pipe3.setOwnerUUID(UUID.randomUUID());
		pipe4.setOwnerUUID(UUID.randomUUID());
		pipe5.setOwnerUUID(UUID.randomUUID());
		
		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		TeleportManager.instance.add(pipe4, 3);
		TeleportManager.instance.add(pipe5, 7);

		ArrayList<PipeBehaviorTeleportItems> pipesList = (ArrayList)TeleportManager.instance.getConnectedPipes(pipe1, false, true);
		
		Log.info(">> getConnectedPipes() returned " + pipesList.size() + " pipes");

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);	
		
		if(pipesList.size() == 2)
		{
			if(pipesList.contains(pipe2) && pipesList.contains(pipe3))
			{
				if(!pipesList.contains(pipe4))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	
}
