module Api::V2
  class JobApi < Grape::API
    namespace :jobs do

      params do
        requires :mime_type, type: String
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
        requires :id_dest, type: String
      end

      post 'move_1_cloud' do
        token = params[:token]
        if params[:mime_type] == 'folder'
          if params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.move_folder(params[:id], params[:id_dest])
            present :time, Time.now.to_s
            present :status, 'success'
            present :message, nil
            present :data, nil
          end
        else
          if params[:type] == 'box'
            client = Boxr::Client.new(token)
            client.move_file(params[:id], params[:id_dest])
            present :time, Time.now.to_s
            present :status, 'success'
            present :message, nil
            present :data, nil
          end
        end
      end

      params do
        requires :id, type: String
        requires :type, type: String
        requires :token, type: String
        requires :id_dest, type: String
      end

      post 'copy_1_cloud' do
        token = params[:token]
        if params[:type] == 'box'
          client = Boxr::Client.new(token)
          client.copy_folder(params[:id], params[:id_dest])
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, nil
          present :data, nil
        end
      end




    end
  end
end
