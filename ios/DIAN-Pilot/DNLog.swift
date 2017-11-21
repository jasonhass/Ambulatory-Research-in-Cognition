//
//  DNLog.swift
//  ARC
//
//  Created by Michael Votaw on 6/14/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

class DNLogManager: NSObject {

    static public let sharedInstance = DNLogManager();
    
    var logToFile:Bool = false;
    
    var fh: FileHandle?;
    var fm = FileManager.default;
    var logName:String = "DNLog.txt";
    
    static public func setLogToFile(log: Bool)
    {
        DNLogManager.sharedInstance.logToFile = log;
    }
    
    static public func logString(_ s:String)
    {
        DNLogManager.sharedInstance.logString(s);
    }
    
    private func logString(_ s:String)
    {
        if logToFile == false
        {
            return;
        }
        
        let documentsDirecotry = fm.urls(for: .documentDirectory, in: .userDomainMask)[0];
        let filepath = documentsDirecotry.appendingPathComponent(logName);
        let line = "\(Date().localizedString(dateStyle: .short, timeStyle: .medium)): \(s)\n";
        
        do
        {
            if fm.fileExists(atPath: filepath.path) == false
            {
                try line.write(to: filepath, atomically: true, encoding: .utf8);
            }
            else
            {
                if fh == nil
                {
                    self.fh = try FileHandle(forWritingTo: filepath);
                }
                
                if let fileHandle = fh
                {
                    _ = fileHandle.seekToEndOfFile();
                    if let data = line.data(using: String.Encoding.utf8) {
                        fileHandle.write(data)
                    }
                }
            }
        }
        catch
        {
            print("error writing to log: \(error)");
        }
    }
    
}

func DNLog(_ s: String, quiet:Bool = false)
{
    if !quiet
    {
        print(s);
    }
    
    if DNLogManager.sharedInstance.logToFile
    {
        DNLogManager.logString(s);
    }
}
