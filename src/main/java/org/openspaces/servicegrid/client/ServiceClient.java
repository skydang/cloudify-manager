package org.openspaces.servicegrid.client;

import java.net.URL;

import org.openspaces.servicegrid.model.service.ServiceOrchestratorState;
import org.openspaces.servicegrid.model.service.ServiceTask;
import org.openspaces.servicegrid.model.tasks.Task;
import org.openspaces.servicegrid.model.tasks.TaskExecutorState;
import org.openspaces.servicegrid.rest.executors.TaskExecutorStatePollingReader;
import org.openspaces.servicegrid.rest.executors.TaskExecutorStateWriter;
import org.openspaces.servicegrid.rest.http.HttpEtag;
import org.openspaces.servicegrid.rest.tasks.StreamConsumer;
import org.openspaces.servicegrid.rest.tasks.StreamProducer;

import com.google.common.base.Preconditions;

public class ServiceClient {

	private final TaskExecutorStateWriter stateWriter;
	private final TaskExecutorStatePollingReader stateReader;
	private final StreamProducer taskProducer;
	private final StreamConsumer taskConsumer;

	public ServiceClient(
			TaskExecutorStatePollingReader stateReader, 
			TaskExecutorStateWriter stateWriter,
			StreamConsumer taskConsumer,
			StreamProducer taskProducer) {
		this.stateReader = stateReader;
		this.stateWriter = stateWriter;
		this.taskConsumer = taskConsumer;
		this.taskProducer = taskProducer;
	}

	public void createService(URL serviceId) {
		final ServiceOrchestratorState orchestratorState = new ServiceOrchestratorState();
		stateWriter.put(serviceId, orchestratorState, HttpEtag.NOT_EXISTS);
	}
	
	public URL addServiceTask(URL serviceId, ServiceTask task) {
		Preconditions.checkNotNull(serviceId);
		Preconditions.checkNotNull(task);
		task.setTarget(serviceId);
		return taskProducer.addToStream(serviceId, task);
	}
	
	public TaskExecutorState getServiceState(URL serviceId) {
		return stateReader.get(serviceId);
	}

	public Task getTask(URL taskId) {
		return (Task) taskConsumer.getById(taskId);
	}
}