package com.jphaugla.service;

import java.util.concurrent.BlockingQueue;

import com.jphaugla.domain.Transaction;
import com.jphaugla.repository.TransactionRepository;
import com.jphaugla.service.KillableRunner;


class TransactionWriter implements KillableRunner {

	private volatile boolean shutdown = false;
    private TransactionRepository transactionRepository;
	private BlockingQueue<Transaction> queue;


	public TransactionWriter(TransactionRepository transactionRepository, BlockingQueue<Transaction> queue) {
		this.transactionRepository = transactionRepository;
		this.queue = queue;
	}

	@Override
	public void run() {
		Transaction transaction;
		while(!shutdown){				
			transaction = queue.poll(); 
			
			if (transaction!=null){
				try {
					this.transactionRepository.save(transaction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}				
		}				
	}
	
	@Override
    public void shutdown() {
		while(!queue.isEmpty())
			
		shutdown = true;
    }
}
