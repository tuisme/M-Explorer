module Api::V2
  class FolderApi < Grape::API
    namespace :folders do

      params do
        requires :id, type: String
      end

      get ':id' do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: [
            {
              id: "jpg",
              name: "File JPG",
              size: "852",
              thumbnail: "https://feseuel.com/wp-content/uploads/2018/07/business_02.jpg",
              type: "jpg"
            },
            {
              id: "png",
              name: "File PNG",
              size: "852",
              thumbnail: "https://feseuel.com/wp-content/uploads/2018/07/business_02.jpg",
              type: "png"
            },
            {
              id: "mp4",
              name: "File MP4",
              size: "852",
              thumbnail: "https://feseuel.com/wp-content/uploads/2018/07/business_02.jpg",
              type: "mp4"
            },
            {
              id: "mp3",
              name: "File MP3",
              size: "852",
              thumbnail: "https://feseuel.com/wp-content/uploads/2018/07/business_02.jpg",
              type: "mp3"
            }
          ]
        }
      end

    end
  end
end
